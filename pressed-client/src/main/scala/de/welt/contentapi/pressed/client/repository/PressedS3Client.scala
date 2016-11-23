package de.welt.contentapi.pressed.client.repository

import java.time.Instant
import javax.inject.Inject

import de.welt.contentapi.core.client.services.configuration.ContentClientConfig
import de.welt.contentapi.core.client.services.s3.S3Client
import de.welt.contentapi.pressed.models.{ApiPressedSection, PressedReads}
import de.welt.contentapi.utils.Loggable
import play.api.libs.json.{JsError, JsSuccess, Json}

sealed trait PressedS3Client {
  def find(path: String): Option[(ApiPressedSection, Instant)]
}

case class PressedS3ClientImpl @Inject()(s3Client: S3Client, contentClientConfig: ContentClientConfig) extends PressedS3Client with Loggable {

  val bucket = contentClientConfig.aws.s3.pressed.bucket
  val file = contentClientConfig.aws.s3.pressed.file

  override def find(path: String): Option[(ApiPressedSection, Instant)] = {

    s3Client.getWithLastModified(bucket, path + file).flatMap {

      case (json, lastMod) ⇒

        Json.parse(json).validate[ApiPressedSection](PressedReads.apiPressedSectionReads) match {
          case JsSuccess(value, _) ⇒
            Some(value, lastMod)
          case err@JsError(_) ⇒
            log.warn(s"Unable to parse content at '$bucket$path$file'. Reason: '${err.toString}'")
            None
        }
    }

  }
}

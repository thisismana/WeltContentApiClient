package de.welt.contentapi.pressed.client.repository

import java.time.Instant
import javax.inject.Inject

import de.welt.contentapi.core.client.services.configuration.ContentClientConfig
import de.welt.contentapi.core.client.services.s3.S3Client
import de.welt.contentapi.pressed.models.ApiPressedSection
import play.api.libs.json.Json

sealed trait PressedS3Client {
  def find(path: String): Option[(ApiPressedSection, Instant)]
}

case class PressedS3ClientImpl @Inject()(s3Client: S3Client, contentClientConfig: ContentClientConfig) extends PressedS3Client {

  override def find(path: String): Option[(ApiPressedSection, Instant)] = {
    import de.welt.contentapi.pressed.models.PressedReads.apiPressedSectionReads
    val bucket = contentClientConfig.aws.s3.pressed.bucket + path
    val file = contentClientConfig.aws.s3.pressed.file

    val maybeS3ResponseTuple: Option[(String, Instant)] = s3Client
      .getWithLastModified(bucket, file)

    val maybeLastMod: Option[Instant] = maybeS3ResponseTuple.map(_._2)

    val maybeApiPressedSection: Option[ApiPressedSection] = maybeS3ResponseTuple
      .map(_._1).map(Json.parse)
      .flatMap(_.validate[ApiPressedSection](apiPressedSectionReads).asOpt)

    for (a <- maybeApiPressedSection; b <- maybeLastMod) yield (a, b)
  }
}

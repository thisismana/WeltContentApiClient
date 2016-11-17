package de.welt.contentapi.pressed_client.services

import javax.inject.{Inject, Singleton}

import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.core_client.services.configuration.ContentClientConfig
import de.welt.contentapi.core_client.services.contentapi.ContentService
import de.welt.contentapi.core_client.services.http.RequestHeaders
import de.welt.contentapi.core_client.services.s3.S3Client
import de.welt.contentapi.pressed.models.ApiPressedContent
import de.welt.contentapi.raw.models.RawChannel
import de.welt.contentapi.utils.Loggable
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}

trait PressedContentService {
  def find(id: String, showRelated: Boolean = true)
          (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiPressedContent]
}

@Singleton
class PressedContentServiceImpl @Inject()(contentService: ContentService, s3Client: S3Client, config: ContentClientConfig)
  extends PressedContentService with Loggable {

  override def find(id: String, showRelated: Boolean = true)
                   (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiPressedContent] =

    contentService.find(id, showRelated).map { response ⇒
      val bucket: String = config.aws.s3.rawTree.bucket
      val file: String = config.aws.s3.rawTree.file

      val maybeResponseRelated: Option[Seq[ApiContent]] = response.related.map(_.toSeq)
      val maybeResponseContent: Option[ApiContent] = Some(response.content)

      s3Client.get(bucket, file).flatMap { tree =>
        Json.parse(tree).validate[RawChannel] match {
          case s: JsSuccess[RawChannel] => s.asOpt
          case e: JsError => log.error(s"JsError parsing S3 file: '%s/%s'." + JsError.toJson(e).toString(), bucket, file)
            Option.empty
        }
      }.map { rawTree =>

        ApiPressedContent(
          content = maybeResponseContent,
          related = maybeResponseRelated,
          channel = None, // TODO: get data from tree
          configuration = None // TODO: get data from tree
        )
      }.getOrElse(
        // Only fallback if s3 get or parse fails
        ApiPressedContent(
          content = maybeResponseContent,
          related = maybeResponseRelated)
      )
    }
}

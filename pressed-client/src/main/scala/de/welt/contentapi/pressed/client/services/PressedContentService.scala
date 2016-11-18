package de.welt.contentapi.pressed.client.services

import javax.inject.{Inject, Singleton}

import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.core_client.services.configuration.ContentClientConfig
import de.welt.contentapi.core_client.services.contentapi.ContentService
import de.welt.contentapi.core_client.services.http.RequestHeaders
import de.welt.contentapi.core_client.services.s3.S3Client
import de.welt.contentapi.pressed.client.converter.RawToApiConverter
import de.welt.contentapi.pressed.models.{ApiChannel, ApiConfiguration, ApiPressedContent}
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
class PressedContentServiceImpl @Inject()(contentService: ContentService, s3Client: S3Client, config: ContentClientConfig, converter: RawToApiConverter)
  extends PressedContentService with Loggable {

  override def find(id: String, showRelated: Boolean = true)
                   (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiPressedContent] =

    contentService.find(id, showRelated).map { response ⇒
      val bucket: String = config.aws.s3.rawTree.bucket
      val file: String = config.aws.s3.rawTree.file

      val maybeResponseRelated: Option[Seq[ApiContent]] = response.related.map(_.toSeq)
      val responseContent: ApiContent = response.content

      s3Client.get(bucket, file).flatMap { tree =>
        Json.parse(tree).validate[RawChannel] match {
          case s: JsSuccess[RawChannel] => s.asOpt
          case e: JsError => log.error(s"JsError parsing S3 file: '%s/%s'." + JsError.toJson(e).toString(), bucket, file)
            Option.empty
        }
      }.map { rawTree =>

        val maybeRawChannel: Option[RawChannel] = responseContent.sections.flatMap(_.home).flatMap(rawTree.findByPath)
        val maybeApiChannel: Option[ApiChannel] = maybeRawChannel.map(rawChannel => converter.getApiChannelFromRawChannel(rawChannel))
        val apiConfiguration: ApiConfiguration = converter.apiConfiguationFromRawChannelConfiguration(rawTree)

        ApiPressedContent(
          content = Some(responseContent),
          related = maybeResponseRelated,
          channel = maybeApiChannel,
          configuration = Some(apiConfiguration)
        )
      }.getOrElse(
        // Fallback if S3.get or Json.parse fails
        ApiPressedContent(
          content = Some(responseContent),
          related = maybeResponseRelated)
      )
    }
}

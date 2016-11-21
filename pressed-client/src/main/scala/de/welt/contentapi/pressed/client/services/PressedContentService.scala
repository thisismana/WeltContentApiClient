package de.welt.contentapi.pressed.client.services

import javax.inject.{Inject, Singleton}

import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.core.client.services.configuration.ContentClientConfig
import de.welt.contentapi.core.client.services.contentapi.ContentService
import de.welt.contentapi.core.client.services.http.RequestHeaders
import de.welt.contentapi.core.client.services.s3.S3Client
import de.welt.contentapi.pressed.client.converter.RawToApiConverter
import de.welt.contentapi.pressed.models.{ApiChannel, ApiConfiguration, ApiPressedContent}
import de.welt.contentapi.raw.models.RawChannel
import de.welt.contentapi.utils.Loggable
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}

trait PressedContentService {
  def find(id: String, showRelated: Boolean = true)
          (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiPressedContent]

  def convert(apiContent: ApiContent, related: Option[Seq[ApiContent]]): ApiPressedContent
}

@Singleton
class PressedContentServiceImpl @Inject()(contentService: ContentService, s3Client: S3Client, config: Configuration, converter: RawToApiConverter)
  extends PressedContentService with Loggable {

  override def find(id: String, showRelated: Boolean = true)
                   (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiPressedContent] =
    contentService
      .find(id, showRelated)
      .map(response ⇒ convert(response.content, response.related))

  override def convert(apiContent: ApiContent, maybeRelatedContent: Option[Seq[ApiContent]]): ApiPressedContent = {

    // todo (mana): extract s3 and caching as own service
    val bucket: String = config.getString("welt.aws.s3.rawTree.bucket")
      .getOrElse(throw config.reportError("welt.aws.s3.rawTree.bucket", "rawTree bucket not configured"))
    val file: String = config.getString("welt.aws.s3.rawTree.file")
      .getOrElse(throw config.reportError("welt.aws.s3.rawTree.file", "rawTree file not configured"))

    s3Client.get(bucket, file).flatMap { tree ⇒
      import de.welt.contentapi.raw.models.RawReads._
      Json.parse(tree).validate[RawChannel] match {
        case s: JsSuccess[RawChannel] ⇒
          s.asOpt
        case e: JsError ⇒
          log.error(f"JsError parsing S3 file: '$bucket%s/$file%s'. " + JsError.toJson(e).toString())
          None
      }
    }.map { rawTree =>

      val maybeRawChannel: Option[RawChannel] = apiContent
        .sections
        .flatMap(_.home)
        .flatMap(rawTree.findByPath)

      val maybeApiChannel: Option[ApiChannel] = maybeRawChannel
        .map(converter.getApiChannelFromRawChannel)

      val apiConfiguration: ApiConfiguration = converter
        .apiConfigurationFromRawChannelConfiguration(rawTree)

      ApiPressedContent(
        content = apiContent,
        related = maybeRelatedContent,
        channel = maybeApiChannel,
        configuration = Some(apiConfiguration)
      )
    } getOrElse {
      // Fallback if S3.get or Json.parse fails
      ApiPressedContent(
        content = apiContent,
        related = maybeRelatedContent)
    }
  }
}

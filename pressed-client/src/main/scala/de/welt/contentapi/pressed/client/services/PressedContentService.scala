package de.welt.contentapi.pressed.client.services

import javax.inject.{Inject, Singleton}

import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.core.client.services.contentapi.ContentService
import de.welt.contentapi.core.client.services.http.RequestHeaders
import de.welt.contentapi.pressed.client.converter.RawToApiConverter
import de.welt.contentapi.pressed.models.{ApiChannel, ApiConfiguration, ApiPressedContent}
import de.welt.contentapi.raw.models.RawChannel
import de.welt.contentapi.raw_client.services.RawTreeService
import de.welt.contentapi.utils.Env.Live
import de.welt.contentapi.utils.Loggable

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}

trait PressedContentService {
  def find(id: String, showRelated: Boolean = true)
          (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiPressedContent]

  def convert(apiContent: ApiContent, related: Option[Seq[ApiContent]]): ApiPressedContent
}

@Singleton
class PressedContentServiceImpl @Inject()(contentService: ContentService, converter: RawToApiConverter, rawTreeService: RawTreeService)
  extends PressedContentService with Loggable {

  override def find(id: String, showRelated: Boolean = true)
                   (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiPressedContent] =
    contentService
      .find(id, showRelated)
      .map(response ⇒ convert(response.content, response.related))

  override def convert(apiContent: ApiContent, maybeRelatedContent: Option[Seq[ApiContent]]): ApiPressedContent = {
    // todo (harry): check env-awareness?
    rawTreeService.root(Live).map { rawTree =>

      val maybeRawChannel: Option[RawChannel] = apiContent
        .sections
        .flatMap(_.home)
        .flatMap(rawTree.findByPath)

      val maybeApiChannel: Option[ApiChannel] = maybeRawChannel
        .map(converter.getApiChannelFromRawChannel)

      val apiConfiguration: ApiConfiguration = converter
        .apiConfigurationFromRawChannel(rawTree)

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

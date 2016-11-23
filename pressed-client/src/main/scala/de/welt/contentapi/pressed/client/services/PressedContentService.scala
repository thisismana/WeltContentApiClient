package de.welt.contentapi.pressed.client.services

import javax.inject.{Inject, Singleton}

import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.core.client.services.contentapi.ContentService
import de.welt.contentapi.core.client.services.http.RequestHeaders
import de.welt.contentapi.pressed.client.converter.RawToApiConverter
import de.welt.contentapi.pressed.models.{ApiChannel, ApiConfiguration, ApiPressedContent}
import de.welt.contentapi.raw.models.RawChannel
import de.welt.contentapi.raw_client.services.RawTreeService
import de.welt.contentapi.utils.Env.{Env, Live}
import de.welt.contentapi.utils.Loggable

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}

trait PressedContentService {
  def find(id: String, showRelated: Boolean = true)
          (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext, env: Env = Live): Future[ApiPressedContent]

  def convert(apiContent: ApiContent, related: Option[Seq[ApiContent]] = None, env: Env = Live): ApiPressedContent
}

@Singleton
class PressedContentServiceImpl @Inject()(contentService: ContentService, converter: RawToApiConverter, rawTreeService: RawTreeService)
  extends PressedContentService with Loggable {

  override def find(id: String, showRelated: Boolean = true)
                   (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext, env: Env = Live): Future[ApiPressedContent] =
    contentService
      .find(id, showRelated)
      .map(response ⇒ convert(response.content, response.related, env))

  override def convert(apiContent: ApiContent, maybeRelatedContent: Option[Seq[ApiContent]] = None, env: Env = Live): ApiPressedContent = {
    rawTreeService.root(env).map { rawTree =>
      val maybeRawChannel: Option[RawChannel] = apiContent
        .sections
        .flatMap(_.home)
        .flatMap(rawTree.findByPath)

      val maybeApiChannel: Option[ApiChannel] = maybeRawChannel
        .map(converter.getApiChannelFromRawChannel)

      val maybeApiConfiguration: Option[ApiConfiguration] = maybeRawChannel.map(converter.apiConfigurationFromRawChannel)

      ApiPressedContent(
        content = apiContent,
        related = maybeRelatedContent,
        channel = maybeApiChannel,
        configuration = maybeApiConfiguration
      )
    } getOrElse {
      // Fallback if S3.get or Json.parse fails
      ApiPressedContent(
        content = apiContent,
        related = maybeRelatedContent)
    }
  }
}

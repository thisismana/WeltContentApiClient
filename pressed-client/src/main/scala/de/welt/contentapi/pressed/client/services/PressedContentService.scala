package de.welt.contentapi.pressed.client.services

import javax.inject.{Inject, Singleton}

import de.welt.contentapi.core.client.services.contentapi.ContentService
import de.welt.contentapi.core.client.services.http.RequestHeaders
import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.pressed.client.converter.RawToApiConverter
import de.welt.contentapi.pressed.models.{ApiChannel, ApiConfiguration, ApiPressedContent}
import de.welt.contentapi.raw.client.services.RawTreeService
import de.welt.contentapi.raw.models.RawChannel
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

  // TODO (harry) add metrics with timings
  override def find(id: String, showRelated: Boolean = true)
                   (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext, env: Env = Live): Future[ApiPressedContent] =
    contentService
      .find(id, showRelated)
      .map(response ⇒ convert(response.content, response.related, env))

  override def convert(apiContent: ApiContent, maybeRelatedContent: Option[Seq[ApiContent]] = None, env: Env = Live): ApiPressedContent = {
    val maybeRelatedPressedContent: Option[Seq[ApiPressedContent]] = maybeRelatedContent
      .map(related ⇒ related.map(content ⇒ convertRelatedContent(content, env = env)))

    findRawChannel(apiContent, env = env).map { rawChannel ⇒
      val apiChannel: ApiChannel = converter.apiChannelFromRawChannel(rawChannel)
      val apiConfiguration: ApiConfiguration = converter.apiConfigurationFromRawChannel(rawChannel)
      ApiPressedContent(
        content = apiContent,
        related = maybeRelatedPressedContent,
        channel = Some(apiChannel),
        configuration = Some(apiConfiguration)
      )
    } getOrElse {
      // Fallback if S3.get or Json.parse fails
      ApiPressedContent(
        content = apiContent,
        related = maybeRelatedPressedContent
      )
    }
  }

  private def convertRelatedContent(apiContent: ApiContent, env: Env = Live): ApiPressedContent =
    ApiPressedContent(
      content = apiContent,
      channel = findRawChannel(apiContent, env = env).map(converter.apiChannelFromRawChannel)
    )

  private def findRawChannel(apiContent: ApiContent, env: Env = Live): Option[RawChannel] = rawTreeService.root(env)
    .flatMap { rawTree ⇒
      apiContent
        .sections
        .flatMap(_.home)
        .flatMap(rawTree.findByPath)
    }
}

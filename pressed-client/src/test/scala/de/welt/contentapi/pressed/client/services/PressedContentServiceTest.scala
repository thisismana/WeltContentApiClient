package de.welt.contentapi.pressed.client.services

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import de.welt.contentapi.core.client.services.contentapi.ContentService
import de.welt.contentapi.core.models.{ApiContent, ApiResponse, ApiSectionData}
import de.welt.contentapi.pressed.client.TestTraits.{TestScopeSimple, TestScopeWithParents}
import de.welt.contentapi.pressed.client.converter.RawToApiConverter
import de.welt.contentapi.pressed.models.ApiPressedContent
import de.welt.contentapi.raw_client.services.RawTreeService
import org.scalatest.mock.MockitoSugar
import org.scalatest.words.MustVerb
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Mockito._

import scala.concurrent.{Await, ExecutionContext, Future}

class PressedContentServiceTest extends FlatSpec
  with Matchers with MustVerb with MockitoSugar
  with TestScopeWithParents with TestScopeSimple {

  implicit lazy val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  // Setup
  val contentService: ContentService = mock[ContentService]
  val converter: RawToApiConverter = new RawToApiConverter();
  val rawTreeService: RawTreeService= mock[RawTreeService]
  val pressedContentService: PressedContentService = new PressedContentServiceImpl(contentService, converter, rawTreeService)

  // Test Content
  val apiContent: ApiContent = ApiContent(
    webUrl = s"${homeSportFussball.id.path}article1234567890/title.html",
    `type` = "news",
    sections = Some(
      ApiSectionData(
        // Note: The home section must be part of the 'rawTree' aka 'home' used in the PressedContentService
        home = Some(homeSportFussball.id.path)
      )
    )
  )
  val relatedAsOptionSeq: Some[List[ApiContent]] = Some(List(
    ApiContent(
      webUrl = s"${homeSportFussball.id.path}article0123456789/titleRelated.html",
      `type` = "news"
    )
  ))
  val eventualApiResponse: Future[ApiResponse] = Future.successful {
    ApiResponse(
      apiContent,
      related = relatedAsOptionSeq
    )
  }

  "PressedContentService" must "enrich an ApiResponse based on its home section" in {
    // Given
    when(contentService
      .find("1234567890", showRelated = true)(None, executionContext))
      .thenReturn(eventualApiResponse)
    when(rawTreeService.get).thenReturn(Some(home))

    // When
    val eventualPressedContent: Future[ApiPressedContent] = pressedContentService.find("1234567890", showRelated = true)(None, executionContext)
    val apiPressedContent: ApiPressedContent = Await.result(eventualPressedContent, Timeout(1L, TimeUnit.SECONDS).duration)

    // Then
    // Channel is enriched by finding the homeSection
    apiPressedContent.channel.flatMap(_.section).flatMap(_.href) shouldBe Some(homeSportFussball.id.path)
    // Config is enriched by finding the homeSection
    apiPressedContent.configuration.flatMap(_.commercial).flatMap(_.pathForAdTag) shouldBe Some("sonstiges")
    // Content from ApiResponse is present
    apiPressedContent.content.id shouldBe apiContent.id
    // Related from ApiResponse is present
    apiPressedContent.related shouldBe relatedAsOptionSeq
  }

}

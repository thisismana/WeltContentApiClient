package de.welt.contentapi.pressed.client.services

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

import akka.util.Timeout
import de.welt.contentapi.core.client.services.exceptions.HttpClientErrorException
import de.welt.contentapi.core.client.services.http.RequestHeaders
import de.welt.contentapi.core.models.ApiReference
import de.welt.contentapi.pressed.client.repository.{PressedDiggerClient, PressedS3Client}
import de.welt.contentapi.pressed.models.{ApiChannel, ApiPressedSection}
import de.welt.contentapi.utils.Env
import de.welt.contentapi.utils.Env.Preview
import org.apache.http.HttpStatus
import org.mockito.Mockito._
import org.mockito.{Matchers ⇒ MockitoMatchers}
import org.scalatest.mock.MockitoSugar
import org.scalatest.words.MustVerb
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class PressedSectionServiceTest extends FlatSpec
  with Matchers with MustVerb with MockitoSugar {

  implicit val ec = ExecutionContext.global
  implicit val requestHeaders: RequestHeaders = Seq.empty

  trait TestScope {
    val mockedS3Client: PressedS3Client = mock[PressedS3Client]
    val mockedDiggerClient: PressedDiggerClient = mock[PressedDiggerClient]
    val pressService: PressedSectionService = new PressedSectionServiceImpl(mockedS3Client, mockedDiggerClient)

    val pressedSection = ApiPressedSection(channel = Some(ApiChannel(Some(ApiReference(Some("label"), Some("/href/"))))))

    val validS3Response = (pressedSection, Instant.now.minus(PressedSectionService.ttlInMinutes - 5, ChronoUnit.MINUTES))
    val invalidS3Response = (pressedSection, Instant.now.minus(PressedSectionService.ttlInMinutes + 5, ChronoUnit.MINUTES))

    val pressedSectionFromDigger: Future[ApiPressedSection] = Future.successful { pressedSection }

    val sectionPath = "/foo/"
  }

  "PressedSectionService" must "deliver S3 version if present and within ttl" in new TestScope {

    // Given
    when(mockedS3Client.find(sectionPath))
      .thenReturn(Some(validS3Response))
    // Mocking just to prove that the section can only come from S3
    when(mockedDiggerClient.findByPath(sectionPath))
      .thenThrow(HttpClientErrorException(HttpStatus.SC_NOT_FOUND, "In this test I dont deliver responses", sectionPath))

    // When
    private val resolvedPressedSection: ApiPressedSection = Await.result(pressService.findByPath(sectionPath), 1.second)

    // Then
    resolvedPressedSection.channel.flatMap(_.section).flatMap(_.href) shouldBe pressedSection.channel.flatMap(_.section).flatMap(_.href)
  }

  it must "ask Digger if S3 Pressed Section is not present" in new TestScope {
    // Given
    when(mockedS3Client.find(sectionPath))
      .thenReturn(None)
    when(mockedDiggerClient.findByPath(sectionPath))
      .thenReturn(pressedSectionFromDigger)

    // When
    Await.result(awaitable = pressService.findByPath(sectionPath), 1.second)

    // Then
    verify(mockedDiggerClient, atLeastOnce).findByPath(sectionPath)
  }

  it must "ask Digger if S3 Pressed Section is too old" in new TestScope {
    // Given
    when(mockedS3Client.find(sectionPath))
      .thenReturn(Some(invalidS3Response))
    when(mockedDiggerClient.findByPath(sectionPath))
      .thenReturn(pressedSectionFromDigger)

    // When
    Await.result(awaitable = pressService.findByPath(sectionPath), 1.second)

    // Then
    verify(mockedDiggerClient, atLeastOnce).findByPath(sectionPath)
  }

  it must "don't ask S3 if Env == Preview" in new TestScope {
    // Given
    when(mockedDiggerClient.findByPath(sectionPath, Preview)).thenReturn(pressedSectionFromDigger)

    // When
    Await.result(awaitable = pressService.findByPath(sectionPath, Env.Preview), 1.second)

    // Then
    verify(mockedS3Client, never).find(sectionPath)
  }

  it must "pass the (preview) env to the service" in {

    // given
    val client = mock[PressedDiggerClient]
    when(client.findByPath("/preview/test", Preview)).thenReturn(Future.successful(ApiPressedSection()))

    // when
    Await.result(new PressedSectionServiceImpl(mock[PressedS3Client], client).findByPath("/preview/test", Preview), 1.second)

    // then
    verify(client).findByPath(MockitoMatchers.eq("/preview/test"), MockitoMatchers.eq(Preview))(MockitoMatchers.any(), MockitoMatchers.any())
  }

}

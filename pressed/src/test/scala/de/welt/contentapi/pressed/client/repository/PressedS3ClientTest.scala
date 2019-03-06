package de.welt.contentapi.pressed.client.repository

import java.time.Instant

import de.welt.contentapi.core.client.services.aws.s3.S3Client
import de.welt.contentapi.core.models.ApiReference
import de.welt.contentapi.pressed.models.{ApiChannel, ApiPressedSection, ApiPressedSectionResponse}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class PressedS3ClientTest extends PlaySpec with MockitoSugar {

  trait TestScope {

    import de.welt.contentapi.pressed.models.PressedWrites.apiPressedSectionResponseWrites

    val path = "/foo/bar/"
    val s3Client: S3Client = mock[S3Client]
    val pressedS3Client: PressedS3Client = new PressedS3ClientImpl(s3Client)

    val simpleSection = ApiPressedSection(
      channel = Some(
        ApiChannel(
          section = Some(ApiReference(
            label = Some("label")
          ))
        )
      )
    )

    val simpleSectionJson: String = Json.toJson(ApiPressedSectionResponse(Some(simpleSection), "test")).toString()
  }

  "PressedS3Client" must {

    "deliver tuple of content and s3 lastMod " in new TestScope {
      // Given
      private val s3LastMod: Instant = Instant.now
      val s3Response = (simpleSectionJson, s3LastMod)

      when(s3Client.getWithLastModified(any(), any())).thenReturn(Some(s3Response))

      // When
      private val Some(tuple) = pressedS3Client.find(path)
      private val (response, timestamp) = tuple

      // Then
      // compare the label, because equals method is not implemented
      response.section.flatMap(_.channel).flatMap(_.section).flatMap(_.label) shouldBe simpleSection.channel.flatMap(_.section).flatMap(_.label)
      // lastMod should be present
      timestamp shouldBe s3LastMod

    }

    "deliver None for AmazonS3Exception(404)" in new TestScope {
      // Given
      private val s3LastMod: Instant = Instant.now
      val s3Response = (simpleSectionJson, s3LastMod)

      when(s3Client.getWithLastModified(any(), any())).thenReturn(None)

      // When -> Then
      pressedS3Client.find(path) shouldBe empty
    }
  }


}

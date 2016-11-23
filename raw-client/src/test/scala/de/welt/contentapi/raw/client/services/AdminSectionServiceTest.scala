package de.welt.contentapi.raw.client.services

import de.welt.contentapi.core.client.services.s3.S3Client
import de.welt.contentapi.raw.models.FullRawChannelWrites.channelWrites
import de.welt.contentapi.raw.models.RawChannelConfiguration
import de.welt.contentapi.utils.Env.Live
import de.welt.testing.DisabledCache
import de.welt.testing.testHelper.raw.channel.emptyWithId
import org.mockito.{Matchers, Mockito}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.{Configuration, Environment}

class AdminSectionServiceTest extends PlaySpec with MockitoSugar {

  trait Fixture {
    val s3 = mock[S3Client]
    val configData = Map(
      RawTreeServiceImpl.bucketConfigKey → "le-bucket",
      RawTreeServiceImpl.fileConfigKey → "le-file")
    private val configuration = Configuration.from(configData
    )
    val asService = new AdminSectionServiceImpl(configuration, s3, Environment.simple(), mock[SdpSectionDataService], DisabledCache)
  }

  "AdminSectionService" should {

    "call save when updating Channel" in new Fixture {
      implicit val env = Live

      // given
      val root = emptyWithId(0)
      Mockito.when(s3.get(Matchers.anyString(), Matchers.anyString())).thenReturn(Some(Json.toJson(root)(channelWrites).toString))

      // when
      asService.updateChannel(root, RawChannelConfiguration(), "le-user", None)

      // then
      val bucket = configData.getOrElse(RawTreeServiceImpl.bucketConfigKey, "")
      val file = configData.getOrElse(RawTreeServiceImpl.fileConfigKey, "")
      Mockito.verify(s3).putPrivate(Matchers.eq(bucket), Matchers.startsWith(file), Matchers.anyString(), Matchers.contains("json"))

    }
  }

}

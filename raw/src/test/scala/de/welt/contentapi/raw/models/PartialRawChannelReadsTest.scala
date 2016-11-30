package de.welt.contentapi.raw.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, Json}

class PartialRawChannelReadsTest extends PlaySpec {

  "PartialRawChannelReads" should {

    trait Fixture {
      val j = JsObject(Map(
        "id" → JsObject(Map(
          "path" → JsString("le-path"),
          "label" → JsString("id"),
          "escenicId" → JsNumber(1337)
        )),
        "config" → Json.toJson(RawChannelConfiguration())(RawWrites.rawChannelConfigurationWrites),
        "metadata" → Json.toJson(RawMetadata())(RawWrites.rawMetadataWrites),
        "stages" → JsArray(Seq(
          Json.toJson(RawChannelStage(Json.toJson(
            RawChannelStageCustomModule(
              index = 1,
              module = "module",
              sourceOverride = Some("source-override")))(RawWrites.rawChannelStageContentWrites)))(RawWrites.rawChannelStageWrites)))
      ))
    }

    "read json with the no childen reads" in new Fixture {
      println(Json.prettyPrint(j))
      val ch: RawChannel = j.result.validate[RawChannel](PartialRawChannelReads.noChildrenReads).get

      ch.id.path must be("le-path")
      val Some(stages) = ch.stages
      stages must be(Seq(RawChannelStageCustomModule(index = 1, module = "module", sourceOverride = Some("source-override"))))

    }
  }

}

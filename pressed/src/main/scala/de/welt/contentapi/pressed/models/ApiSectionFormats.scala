package de.welt.contentapi.pressed.models

import play.api.libs.json.Json

object ApiSectionFormat {

  implicit val apiCommercialFormat = Json.format[ApiCommercial]

  implicit val teaserConfigFormat = Json.format[ApiTeaserConfig]
  implicit val teaserFormat = Json.format[ApiTeaser]
  implicit val stageFormat = Json.format[ApiStage]
}

object ApiSectionWrites {

  implicit val apiCommercialWrites = Json.writes[ApiCommercial]

  implicit val teaserConfigWrites = Json.writes[ApiTeaserConfig]
  implicit val teaserWrites = Json.writes[ApiTeaser]
  implicit val stageWrites = Json.writes[ApiStage]
}

object SectionPageReads {

  implicit val apiCommercialReads = Json.reads[ApiCommercial]

  implicit val teaserConfigReads = Json.reads[ApiTeaserConfig]
  implicit val teaserReads = Json.reads[ApiTeaser]
  implicit val stageReads = Json.reads[ApiStage]
}

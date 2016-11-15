package de.welt.contentapi.core.models.section

import de.welt.contentapi.core.models.section.commercial.ApiCommercial
import de.welt.contentapi.core.models.section.stage.{ApiStage, ApiTeaser, ApiTeaserConfig}
import play.api.libs.json.Json

object ApiSectionFormat {

  import de.welt.contentapi.core.models.content.ApiContentFormats._

  implicit val apiCommercialFormat = Json.format[ApiCommercial]

  implicit val teaserConfigFormat = Json.format[ApiTeaserConfig]
  implicit val teaserFormat = Json.format[ApiTeaser]
  implicit val stageFormat = Json.format[ApiStage]
  implicit val sectionPageFormat = Json.format[ApiSection]
}

object ApiSectionWrites {

  import de.welt.contentapi.core.models.content.ApiContentWrites._

  implicit val apiCommercialWrites = Json.writes[ApiCommercial]

  implicit val teaserConfigWrites = Json.writes[ApiTeaserConfig]
  implicit val teaserWrites = Json.writes[ApiTeaser]
  implicit val stageWrites = Json.writes[ApiStage]
  implicit val sectionPageWrites = Json.writes[ApiSection]
}

object SectionPageReads {

  import de.welt.contentapi.core.models.content.ApiContentReads._

  implicit val apiCommercialReads = Json.reads[ApiCommercial]

  implicit val teaserConfigReads = Json.reads[ApiTeaserConfig]
  implicit val teaserReads = Json.reads[ApiTeaser]
  implicit val stageReads = Json.reads[ApiStage]
  implicit val sectionPageReads = Json.reads[ApiSection]
}
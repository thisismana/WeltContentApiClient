package de.welt.contentapi.core.models.section

import de.welt.contentapi.core.models.section.commercial.ApiCommercial
import de.welt.contentapi.core.models.section.stage.{Stage, Teaser, TeaserConfig}
import play.api.libs.json.Json

object SectionPageFormats {

  import de.welt.contentapi.core.models.content.ApiContentFormats._

  implicit val apiCommercialFormat = Json.format[ApiCommercial]

  implicit val teaserConfigFormat = Json.format[TeaserConfig]
  implicit val teaserFormat = Json.format[Teaser]
  implicit val stageFormat = Json.format[Stage]
  implicit val sectionPageFormat = Json.format[ApiSection]
}

object SectionPageWrites {

  import de.welt.contentapi.core.models.content.ApiContentWrites._

  implicit val apiCommercialWrite = Json.writes[ApiCommercial]

  implicit val teaserConfigWrite = Json.writes[TeaserConfig]
  implicit val teaserWrite = Json.writes[Teaser]
  implicit val stageWrite = Json.writes[Stage]
  implicit val sectionPageWrite = Json.writes[ApiSection]
}

object SectionPageReads {

  import de.welt.contentapi.core.models.content.ApiContentReads._

  implicit val apiCommercialRead = Json.reads[ApiCommercial]

  implicit val teaserConfigRead = Json.reads[TeaserConfig]
  implicit val teaserRead = Json.reads[Teaser]
  implicit val stageRead = Json.reads[Stage]
  implicit val sectionPageRead = Json.reads[ApiSection]
}
package de.welt.contentapi.core.models.section

import de.welt.contentapi.core.models.section.commercial.ApiCommercial
import de.welt.contentapi.core.models.section.stage.{Stage, Teaser, TeaserConfig}
import play.api.libs.json.Json

object SectionPageFormats {
  implicit val apiCommercialFormat = Json.format[ApiCommercial]

  implicit val teaserConfigFormat = Json.format[TeaserConfig]
  implicit val teaserFormat = Json.format[Teaser]
  implicit val stageFormat = Json.format[Stage]
  implicit val sectionPageFormat = Json.format[SectionPage]
}
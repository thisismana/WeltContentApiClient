package de.welt.contentapi.core.models.section

import play.api.libs.json.Json

object SectionPageFormats {
  implicit val sectionPageFormat = Json.format[SectionPage]
}
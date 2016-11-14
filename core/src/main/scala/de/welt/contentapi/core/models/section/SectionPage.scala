package de.welt.contentapi.core.models.section

import de.welt.contentapi.core.models.section.stage.Stage
import play.api.libs.json.Json

case class SectionPage(stages: Seq[Stage])

object format {
  implicit val sectionPageFormat = Json.format[SectionPage]
}


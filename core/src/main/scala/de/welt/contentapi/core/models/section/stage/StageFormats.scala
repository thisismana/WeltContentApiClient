package de.welt.contentapi.core.models.section.stage

import play.api.libs.json.Json

object StageFormats {
  implicit val stageFormat = Json.format[Stage]
}



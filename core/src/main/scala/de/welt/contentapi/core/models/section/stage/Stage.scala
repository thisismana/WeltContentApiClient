package de.welt.contentapi.core.models.section.stage

import de.welt.contentapi.core.models.content.ApiContent
import play.api.libs.json.Json

case class Stage(stageLayoutId: String,
                 stageLabel: Option[String],
                 teasers: Seq[ApiContent]
                )

object format {
  implicit val stageFormat = Json.format[Stage]
}




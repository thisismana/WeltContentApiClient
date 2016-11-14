package de.welt.contentapi.core.models.section.stage

import de.welt.contentapi.core.models.content.ApiContent

case class Stage(stageLayoutId: String,
                 stageLabel: Option[String],
                 teasers: Seq[ApiContent]
                )



package de.welt.contentapi.core.models.section.stage

case class Stage(stageLayoutId: String,
                 stageLabel: Option[String],
                 teasers: Seq[Teaser]
                )



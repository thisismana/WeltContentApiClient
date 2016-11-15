package de.welt.contentapi.core.models.section.stage

case class ApiStage(stageLayoutId: String,
                    stageLabel: Option[String],
                    teasers: Seq[ApiTeaser]
                )



package de.welt.contentapi.pressed.models

import de.welt.contentapi.core.models.ApiReference


case class ApiStage(teasers: Seq[ApiTeaser],
                    configuration: Option[ApiStageConfiguration] = None)

case class ApiStageConfiguration(layout: String,
                                 label: Option[String],
                                 sectionReference: Option[Seq[ApiReference]] = None)



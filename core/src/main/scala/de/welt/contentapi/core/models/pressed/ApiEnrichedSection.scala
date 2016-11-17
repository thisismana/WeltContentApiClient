package de.welt.contentapi.core.models.pressed

import ApiStage

/**
  * @param channel       channel with breadcrumb of the section. (ConfigMcConfigFace)
  * @param configuration configuration for the section page. (ConfigMcConfigFace)
  */
case class ApiEnrichedSection(stages: Seq[ApiStage] = Nil,
                              channel: Option[ApiChannel] = None,
                              configuration: Option[ApiConfiguration] = None)

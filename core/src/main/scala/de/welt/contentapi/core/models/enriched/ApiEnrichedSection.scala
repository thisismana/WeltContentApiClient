package de.welt.contentapi.core.models.enriched

import de.welt.contentapi.core.models.channel.ApiChannel
import de.welt.contentapi.core.models.configuration.ApiConfiguration
import de.welt.contentapi.core.models.section.ApiSection

/**
  * @param section       all teaser content separated in stages with layout configuration. (Digger)
  * @param channel       channel with breadcrumb of the section. (ConfigMcConfigFace)
  * @param configuration configuration for the section page. (ConfigMcConfigFace)
  */
case class ApiEnrichedSection(section: Option[ApiSection] = None,
                              channel: Option[ApiChannel] = None,
                              configuration: Option[ApiConfiguration] = None)

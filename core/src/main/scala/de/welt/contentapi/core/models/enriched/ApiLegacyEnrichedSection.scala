package de.welt.contentapi.core.models.enriched

import de.welt.contentapi.core.models.legacy.ApiLegacySection
import de.welt.contentapi.core.models.pressed.{ApiChannel, ApiConfiguration}

/**
  * Legacy == papyrus -> da-hood version of sections
  * @param section section with stages with teasers (Da-hood)
  * @param channel channel with breadcrumb (ConfigMcConfigFace)
  * @param configuration configuration for the section page (ConfigMcConfigFace)
  */
case class ApiLegacyEnrichedSection(section: Option[ApiLegacySection] = None,
                                    channel: Option[ApiChannel] = None,
                                    configuration: Option[ApiConfiguration] = None)

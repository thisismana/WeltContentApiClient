package de.welt.contentapi.core.models.enriched

import de.welt.contentapi.core.models.channel.ApiChannel
import de.welt.contentapi.core.models.configuration.ApiConfiguration
import de.welt.contentapi.core.models.content.ApiResponse

/**
  * @param response      content api response with a single content and it's related content. (Frank)
  * @param channel       channel with breadcrumb of the content. (ConfigMcConfigFace)
  * @param configuration configuration for the content page. (ConfigMcConfigFace)
  */
case class ApiEnrichedContent(response: Option[ApiResponse] = None,
                              channel: Option[ApiChannel] = None,
                              configuration: Option[ApiConfiguration] = None)



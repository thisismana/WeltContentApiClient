package de.welt.contentapi.core.models.enriched

import de.welt.contentapi.core.models.channel.ApiChannel
import de.welt.contentapi.core.models.configuration.ApiConfiguration
import de.welt.contentapi.core.models.content.ApiContent

case class ApiEnrichedTeaser(content: Option[ApiContent] = None,
                             channel: Option[ApiChannel] = None)

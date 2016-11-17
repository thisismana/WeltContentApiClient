package de.welt.contentapi.core.models.enriched

import de.welt.contentapi.core.models.content.ApiContent
import de.welt.contentapi.core.models.pressed.ApiChannel

case class ApiEnrichedTeaser(content: Option[ApiContent] = None,
                             channel: Option[ApiChannel] = None)

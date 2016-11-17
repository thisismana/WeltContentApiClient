package de.welt.contentapi.pressed.models.legacy

import de.welt.contentapi.core.models.content.ApiContent

case class ApiEnrichedTeaser(content: Option[ApiContent] = None,
                             channel: Option[ApiChannel] = None)

package de.welt.contentapi.core.models.funkotron

import de.welt.contentapi.core.models.channel.ApiChannel
import de.welt.contentapi.core.models.configuration.ApiConfiguration


/**
  * /sport/fussball/ => PresentationChannel
  * @param channel
  * @param configuration
  */
case class PresentationChannel(channel: Option[ApiChannel] = None, configuration: Option[ApiConfiguration] = None)



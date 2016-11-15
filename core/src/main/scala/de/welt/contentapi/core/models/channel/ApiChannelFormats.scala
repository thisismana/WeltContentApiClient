package de.welt.contentapi.core.models.channel

import play.api.libs.json.{Format, Json, Reads, Writes}

object ApiChannelFormats {
  import de.welt.contentapi.core.models.configuration.ApiConfigurationFormats._

  implicit lazy val apiChannelFormat: Format[ApiChannel] = Json.format[ApiChannel]
}

object ApiChannelReads {
  import de.welt.contentapi.core.models.configuration.ApiConfigurationReads._

  implicit lazy val apiChannelReads: Reads[ApiChannel] = Json.reads[ApiChannel]
}

object ApiChannelWrites {
  import de.welt.contentapi.core.models.configuration.ApiConfigurationWrites._

  implicit lazy val apiChannelWrites: Writes[ApiChannel] = Json.writes[ApiChannel]
}

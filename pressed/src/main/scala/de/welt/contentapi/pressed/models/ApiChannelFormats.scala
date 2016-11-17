package de.welt.contentapi.pressed.models

import play.api.libs.json.{Format, Json, Reads, Writes}

object ApiChannelFormats {

  implicit lazy val apiChannelFormat: Format[ApiChannel] = Json.format[ApiChannel]
}

object ApiChannelReads {

  implicit lazy val apiChannelReads: Reads[ApiChannel] = Json.reads[ApiChannel]
}

object ApiChannelWrites {

  implicit lazy val apiChannelWrites: Writes[ApiChannel] = Json.writes[ApiChannel]
}

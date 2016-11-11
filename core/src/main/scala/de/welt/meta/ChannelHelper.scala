package de.welt.meta

import de.welt.contentapi.core.models.configuration2.{ApiChannel, ApiChannelData, ChannelId}
import de.welt.contentapi.core.models.{ApiChannelData, ChannelId}

object ChannelHelper {

  def emptyWithId(id: Long) = ApiChannel(ChannelId(path = id.toString, ece = id), ApiChannelData(id.toString))

  def emptyWithIdAndData(id: Long, data: ApiChannelData) = ApiChannel(ChannelId(path = id.toString, ece = id), data)

  def emptyWithIdAndChildren(id: Long, children: Seq[ApiChannel]) = ApiChannel(ChannelId(path = id.toString, ece = id), ApiChannelData(id.toString), children = children)

}

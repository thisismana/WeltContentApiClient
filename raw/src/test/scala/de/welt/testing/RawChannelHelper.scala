package de.welt.testing

import de.welt.contentapi.raw.models.{RawChannel, RawChannelCommercial, RawChannelConfiguration, RawChannelId, RawChannelMetadata}

object RawChannelHelper {

  def emptyWithId(id: Long) =
    RawChannel(RawChannelId(path = id.toString, escenicId = id, label = id.toString))

  def emptyWithIdAndConfig(id: Long, config: RawChannelConfiguration) =
    RawChannel(RawChannelId(path = id.toString, escenicId = id, label = id.toString), config = config)

  def emptyWithIdAndChildren(id: Long, children: Seq[RawChannel]) =
    RawChannel(RawChannelId(path = id.toString, escenicId = id, label = id.toString), children = children)

}

object RawChannelConfigurationHelper {

  def withTitleAndAds(title: String, adsEnabled: Boolean) = RawChannelConfiguration(
    metadata = Some(RawChannelMetadata(title = Some(title))),
    commercial = RawChannelCommercial(definesAdTag = adsEnabled, definesVideoAdTag = adsEnabled)
  )


}
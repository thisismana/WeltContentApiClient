package de.welt.contentapi.raw.models

import de.welt.contentapi.raw.models.legacy._

object ApiChannelToRawChannelConverter {
  def apply(apiChannel: ApiChannel): RawChannel = RawChannel(
    id = rawChannelId(
      channelId = apiChannel.id,
      apiChannelData = apiChannel.data
    ),
    config = null,
    metadata = apiChannel.metadata.map(metadata),
    stages = None
  )

  private def rawChannelId(channelId: ChannelId, apiChannelData: ApiChannelData): RawChannelId = RawChannelId(
    path = channelId.path,
    label = apiChannelData.label,
    escenicId = channelId.ece
  )

  private def config(apiChannelData: ApiChannelData): RawChannelConfiguration = RawChannelConfiguration(
    metaTags = apiChannelData.fields.map(metaTags)
  )

  private def metaTags(fields: Map[String, String]): RawChannelMetaTags = RawChannelMetaTags(
    title = fields.get("title"),
    description = fields.get("description"),
    keywords = fields.get("keywords"),
    contentRobots = contentMetaRobotsContent(fields),
    sectionRobots = contentMetaRobotsSection(fields)
  )

  private def contentMetaRobotsSection(fields: Map[String, String]): Option[RawChannelMetaRobotsTag] = {
    val metaRobotsTag = RawChannelMetaRobotsTag(
      noIndex = fields.get("metaNoIndex"),
      noFollow = fields.get("metaNoFollow")
    )
    metaRobotsTag match {
      case RawChannelMetaRobotsTag(None, None) ⇒ None
      case valid@RawChannelMetaRobotsTag(_, _) ⇒ Some(valid)
    }
  }

  private def contentMetaRobotsContent(fields: Map[String, String]): Option[RawChannelMetaRobotsTag] = {
    val metaRobotsTag = RawChannelMetaRobotsTag(
      noIndex = fields.get("metaArticleNoIndex"),
      noFollow = fields.get("metaArticleNoFollow")
    )
    metaRobotsTag match {
      case RawChannelMetaRobotsTag(None, None) ⇒ None
      case valid@RawChannelMetaRobotsTag(_, _) ⇒ Some(valid)
    }
  }

  private def metadata(apiChannelMetadataNew: ApiChannelMetadataNew): Metadata = Metadata(
    changedBy = apiChannelMetadataNew.changedBy,
    lastModifiedDate = apiChannelMetadataNew.lastModifiedDate
  )

}

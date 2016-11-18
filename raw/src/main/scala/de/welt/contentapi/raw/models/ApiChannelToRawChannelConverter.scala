package de.welt.contentapi.raw.models

import de.welt.contentapi.raw.models.legacy._

object ApiChannelToRawChannelConverter {
  def apply(apiChannel: ApiChannel): RawChannel = RawChannel(
    id = rawChannelId(
      channelId = apiChannel.id,
      apiChannelData = apiChannel.data
    ),
    config = config(apiChannel.data),
    // Info:
    // All old stage configuration are ignored. Currently not used by any app.
    stages = None,
    metadata = apiChannel.metadata.map(metadata),
    // Todo
    parent = None,
    // Todo
    children = None
  )

  private def rawChannelId(channelId: ChannelId, apiChannelData: ApiChannelData): RawChannelId = RawChannelId(
    path = channelId.path,
    label = apiChannelData.label,
    escenicId = channelId.ece
  )

  private def config(apiChannelData: ApiChannelData): Option[RawChannelConfiguration] = {
    val config = RawChannelConfiguration(
      metaTags = apiChannelData.fields.map(metaTags),
      header = header(apiChannelData),
      commercial = commercial(apiChannelData.adData)
    )

    config match {
      case RawChannelConfiguration(None, None, None) ⇒ None
      case valid@RawChannelConfiguration(_, _, _) ⇒ Some(valid)
    }
  }

  private def metaTags(fields: Map[String, String]): RawChannelMetaTags = RawChannelMetaTags(
    title = fields.get("title"),
    description = fields.get("description"),
    keywords = fields.get("keywords").map(_.split(",")),
    contentRobots = contentMetaRobotsContent(fields),
    sectionRobots = contentMetaRobotsSection(fields)
  )

  private def contentMetaRobotsSection(fields: Map[String, String]): Option[RawChannelMetaRobotsTag] = {
    val metaRobotsTag = RawChannelMetaRobotsTag(
      noIndex = fields.get("metaNoIndex").map(_.toBoolean),
      noFollow = fields.get("metaNoFollow").map(_.toBoolean)
    )
    metaRobotsTag match {
      case RawChannelMetaRobotsTag(None, None) ⇒ None
      case valid@RawChannelMetaRobotsTag(_, _) ⇒ Some(valid)
    }
  }

  private def contentMetaRobotsContent(fields: Map[String, String]): Option[RawChannelMetaRobotsTag] = {
    val metaRobotsTag = RawChannelMetaRobotsTag(
      noIndex = fields.get("metaArticleNoIndex").map(_.toBoolean),
      noFollow = fields.get("metaArticleNoFollow").map(_.toBoolean)
    )
    metaRobotsTag match {
      case RawChannelMetaRobotsTag(None, None) ⇒ None
      case valid@RawChannelMetaRobotsTag(_, _) ⇒ Some(valid)
    }
  }

  private def header(apiChannelData: ApiChannelData): Option[RawChannelHeader] = {
    val header = RawChannelHeader(
      sponsoring = apiChannelData.siteBuilding.map(_.theme),
      label = Option(apiChannelData.label).filter(_.nonEmpty),
      // Info:
      // After the migration we split sponsoring and logo.
      // For now: old data == sponsoring
      logo = None,
      // Info:
      // New field. At the moment the slogan is part of the client (Funkotron)
      slogan = None,
      // Info:
      // New field. This is part for some next steps.
      sectionReferences = None
    )

    header match {
      case RawChannelHeader(None, None, None, None, None) ⇒ None
      case valid@RawChannelHeader(_, _, _, _, _) ⇒ Some(valid)
    }
  }

  private def commercial(apiChannelAdData: ApiChannelAdData): Option[RawChannelCommercial] = {
    val commercial = RawChannelCommercial(
      definesAdTag = if (apiChannelAdData.definesAdTag) Some(apiChannelAdData.definesAdTag) else None,
      definesVideoAdTag = apiChannelAdData.definesVideoAdTag
    )

    commercial match {
      case RawChannelCommercial(None, None) ⇒ None
      case valid@RawChannelCommercial(_, _) ⇒ Some(valid)
    }
  }

  private def metadata(apiChannelMetadataNew: ApiChannelMetadataNew): Metadata = Metadata(
    changedBy = apiChannelMetadataNew.changedBy,
    lastModifiedDate = apiChannelMetadataNew.lastModifiedDate,
    // Info:
    // modified & isRessort are new fields
  )

}

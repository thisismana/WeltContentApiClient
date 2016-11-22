package de.welt.contentapi.pressed.client.converter

import de.welt.contentapi.core.models.ApiReference
import de.welt.contentapi.pressed.models._
import de.welt.contentapi.raw.models.{RawChannel, RawChannelMetaRobotsTag, RawSectionReference}

class RawToApiConverter {

  /** Converter method that takes a rawChannel and returns an ApiChannel from its data
    *
    * @param rawChannel the rawChannel produced by ConfigMcConfigFace
    * @return a new ApiChannel with the data from the rawChannel
    */
  def getApiChannelFromRawChannel(rawChannel: RawChannel): ApiChannel = {
    ApiChannel(
      section = Some(getApiSectionReferenceFromRawChannel(rawChannel)),
      breadcrumb = Some(getBreadcrumb(rawChannel)))
  }

  private[converter] def getApiSectionReferenceFromRawChannel(rawChannel: RawChannel): ApiReference = {
    ApiReference(
      label = Some(rawChannel.id.label),
      href = Some(rawChannel.id.path)
    )
  }

  private[converter] def getBreadcrumb(self: RawChannel): Seq[ApiReference] = {
    var current = self
    var breadcrumbFromRawChannel: Seq[ApiReference] = Seq(ApiReference(
      label = Some(current.id.label),
      href = Some(current.id.path)
    ))
    while (current.parent.isDefined) {
      current = current.parent.get
      breadcrumbFromRawChannel = breadcrumbFromRawChannel :+ ApiReference(
          label = Some(current.id.label),
          href = Some(current.id.path)
      )
    }
    // breadcrumb should start from root channel -> must be reversed
    // / -> /sport/ -> /sport/fussball/
    breadcrumbFromRawChannel.reverse
  }

  /** Converter method that takes a rawChannel and returns an ApiConfiguration from its data
    *
    * @param rawChannel the rawChannel produced by ConfigMcConfigface
    * @return a new ApiConfiguration Object with the data from the rawChannel
    */
  def apiConfigurationFromRawChannel(rawChannel: RawChannel) = ApiConfiguration(
    meta = apiMetaConfigurationFromRawChannel(rawChannel),
    commercial = Some(apiCommercialConfigurationFromRawChannel(rawChannel)),
    sponsoring = apiSponsoringConfigurationFromRawChannel(rawChannel),
    header = Some(apiHeaderConfigurationFromRawChannel(rawChannel)),
    theme = apiThemeFromRawChannel(rawChannel)
  )

  private[converter] def calculatePathForAdTag(rawChannel: RawChannel): String = {
    var currentChannel = rawChannel
    while (!currentChannel.config.commercial.definesAdTag && currentChannel.parent.isDefined) {
      currentChannel = currentChannel.parent.get
    }
    val currentPath: String = currentChannel.id.path
    val lastOption: Option[String] = currentPath.substring(0,currentPath.length).split("/").lastOption
    if (lastOption.isDefined) {
      lastOption.get
    }
    else {
      "sonstiges"
    }
  }

  private[converter] def calculatePathForVideoAdTag(rawChannel: RawChannel): String = {
    var currentChannel = rawChannel
    while (!currentChannel.config.commercial.definesVideoAdTag && currentChannel.parent.isDefined) {
      currentChannel = currentChannel.parent.get
    }
    val currentPath: String = currentChannel.id.path
    val lastOption: Option[String] = currentPath.substring(0,currentPath.length).split("/").lastOption
    if (lastOption.isDefined) {
      lastOption.get
    }
    else {
      "sonstiges"
    }
  }

  private[converter] def apiMetaConfigurationFromRawChannel(rawChannel: RawChannel): Option[ApiMetaConfiguration] = {
    rawChannel.config.metadata.map(metadata => ApiMetaConfiguration(
      title = metadata.title,
      description = metadata.description,
      tags = metadata.keywords,
      contentMetaRobots = metadata.contentRobots.map(apiMetaRobotsFromRawChannelMetaRobotsTag),
      sectionMetaRobots = metadata.sectionRobots.map(apiMetaRobotsFromRawChannelMetaRobotsTag)
    )
    )
  }

  private[converter] def apiMetaRobotsFromRawChannelMetaRobotsTag(rawChannelMetaRobotsTag: RawChannelMetaRobotsTag): ApiMetaRobots =
    ApiMetaRobots(noIndex = rawChannelMetaRobotsTag.noIndex, noFollow = rawChannelMetaRobotsTag.noFollow)

  /** Always calculates adTags, in doubt 'sonstiges' -> not optional
    *
    * @param rawChannel source Channel to take the data from
    * @return a resolved 'ApiCommercialConfiguration' containing a videoAdTag and an adTag
    */
  private[converter] def apiCommercialConfigurationFromRawChannel(rawChannel: RawChannel): ApiCommercialConfiguration = {
    ApiCommercialConfiguration(
      pathForAdTag = Some(calculatePathForAdTag(rawChannel)),
      pathForVideoAdTag = Some(calculatePathForVideoAdTag(rawChannel))
    )
  }

  private[converter] def apiSponsoringConfigurationFromRawChannel(rawChannel: RawChannel): Option[ApiSponsoringConfiguration] = {
    rawChannel.config.header.map {
        header => ApiSponsoringConfiguration(header.sponsoring)
      }
  }

  private[converter] def apiHeaderConfigurationFromRawChannel(rawChannel: RawChannel) = {
    val apiSectionReferences: Seq[ApiReference] = apiSectionReferencesFromRawSectionReferences(
      rawChannel.config.header.map(_.unwrappedSectionReferences).getOrElse(Nil)
    )
    ApiHeaderConfiguration(
      title = rawChannel.config.header.flatMap(_.label),
      sectionReferences = Some(apiSectionReferences)
    )
  }

  private[converter] def apiSectionReferencesFromRawSectionReferences(references: Seq[RawSectionReference]): Seq[ApiReference] = {
    references.map(ref ⇒ ApiReference(ref.label, ref.path))
  }

  private[converter] def apiThemeFromRawChannel(rawChannel: RawChannel): Option[ApiThemeConfiguration] =
    rawChannel.config.theme.map(t ⇒ ApiThemeConfiguration(t.name, t.fields))

}

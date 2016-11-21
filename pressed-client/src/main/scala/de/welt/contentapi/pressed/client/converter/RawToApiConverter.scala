package de.welt.contentapi.pressed.client.converter

import de.welt.contentapi.core.models.ApiReference
import de.welt.contentapi.pressed.models._
import de.welt.contentapi.raw.models.{RawChannel, RawSectionReference}

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
    val breadcrumbFromRawChannel: Seq[ApiReference] = Seq.empty
    while (self.parent.isDefined) {
      val currentParent = self.parent.get
      breadcrumbFromRawChannel :+ ApiReference(
        label = Some(currentParent.id.label),
        href = Some(currentParent.id.path)
      )
    }
    breadcrumbFromRawChannel
  }

  /** Converter method that takes a rawChannel and returns an ApiConfiguration from its data
    *
    * @param rawChannel the rawChannel produced by ConfigMcConfigface
    * @return a new ApiConfiguration Object with the data from the rawChannel
    */
  def apiConfigurationFromRawChannelConfiguration(rawChannel: RawChannel) = ApiConfiguration(
    meta = apiMetaConfigurationFromRawChannel(rawChannel),
    commercial = Some(apiCommercialConfigurationFromRawChannel(rawChannel)),
    sponsoring = apiSponsoringConfigurationFromRawChannel(rawChannel),
    header = Some(apiHeaderConfigurationFromRawChannel(rawChannel)),
    theme = apiThemeFromRawChannel(rawChannel)
  )

  private[converter] def unwrappedDefinesAdTag(rawChannel: RawChannel): Boolean = rawChannel.config.flatMap(_.commercial).exists(_.unwrappedDefinesAdTag)

  private[converter] def unwrappedDefinesVideoAdTag(rawChannel: RawChannel) = rawChannel.config.flatMap(_.commercial).exists(_.unwrappedDefinesVideoAdTag)

  private[converter] def calculateAdTag(rawChannel: RawChannel): String = {
    var currentChannel = rawChannel
    while (!unwrappedDefinesAdTag(currentChannel) && currentChannel.parent.isDefined) {
      currentChannel = currentChannel.parent.get
    }
    val adTag: String = currentChannel.id.path.replaceAll("/", "")
    if (adTag.isEmpty) {
      "sonstiges"
    }
    else {
      adTag
    }
  }

  private[converter] def calculateVideoAdTag(rawChannel: RawChannel): String = {
    var currentChannel = rawChannel
    while (!unwrappedDefinesVideoAdTag(currentChannel) && currentChannel.parent.isDefined) {
      currentChannel = currentChannel.parent.get
    }
    val adTag: String = currentChannel.id.path.replaceAll("/", "")
    if (adTag.isEmpty) {
      "sonstiges"
    }
    else {
      adTag
    }
  }

  private[converter] def apiMetaConfigurationFromRawChannel(rawChannel: RawChannel): Option[ApiMetaConfiguration] = {
    rawChannel.config.flatMap(_.metadata).map(metadata => ApiMetaConfiguration(
      title = metadata.title,
      description = metadata.title,
      tags = metadata.keywords)
    )
  }

  /** Always calculates adTags, in doubt 'sonstiges' -> not optional
    *
    * @param rawChannel source Channel to take the data from
    * @return a resolved 'ApiCommercialConfiguration' containing a videoAdTag and an adTag
    */
  private[converter] def apiCommercialConfigurationFromRawChannel(rawChannel: RawChannel): ApiCommercialConfiguration = {
    ApiCommercialConfiguration(
      adTag = Some(calculateAdTag(rawChannel)),
      videoAdTag = Some(calculateVideoAdTag(rawChannel))
    )
  }

  private[converter] def apiSponsoringConfigurationFromRawChannel(rawChannel: RawChannel): Option[ApiSponsoringConfiguration] = {
    rawChannel
      .config
      .flatMap(_.header)
      .map {
        header => ApiSponsoringConfiguration(header.sponsoring)
      }
  }

  private[converter] def apiHeaderConfigurationFromRawChannel(rawChannel: RawChannel) = {
    val apiSectionReferences: Seq[ApiReference] = apiSectionReferencesFromRawSectionReferences(
      rawChannel.config.flatMap(_.header).map(_.unwrappedSectionReferences).getOrElse(Nil)
    )
    ApiHeaderConfiguration(
      title = rawChannel.config.flatMap(_.header).flatMap(_.label),
      sectionReferences = Some(apiSectionReferences)
    )
  }

  private[converter] def apiSectionReferencesFromRawSectionReferences(references: Seq[RawSectionReference]): Seq[ApiReference] = {
    references.map(ref => ApiReference(ref.label, ref.path))
  }

  private[converter] def apiThemeFromRawChannel(rawChannel: RawChannel): Option[ApiThemeConfiguration] =
    rawChannel.config.flatMap(_.theme.map(t ⇒ ApiThemeConfiguration(t.name, t.fields)))

}

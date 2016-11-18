package de.welt.contentapi.pressed.client.converter

import de.welt.contentapi.core.models.ApiSectionReference
import de.welt.contentapi.pressed.models.{ApiBrandingConfiguration, ApiChannel, ApiCommercialConfiguration, ApiConfiguration, ApiMetaConfiguration}
import de.welt.contentapi.raw.models.{RawChannel, RawChannelCommercial, RawChannelHeader, RawChannelMetaTags}

class RawToApiConverter {

  /** Converter method that takes a rawChannel and returns an ApiChannel from its data
    *
    * @param rawChannel the rawChannel produced by ConfigMcConfigface
    * @return a new ApiChannel with the data from the rawChannel
    */
  def getApiChannelFromRawChannel(rawChannel: RawChannel): ApiChannel = {
    ApiChannel(
      section = Some(getApiSectionReferenceFromRawChannel(rawChannel)),
      breadcrumb = Some(getBreadcrumb(rawChannel)))
  }

  private def getApiSectionReferenceFromRawChannel(rawChannel: RawChannel): ApiSectionReference = {
    ApiSectionReference(
      label = Some(rawChannel.id.label),
      path = Some(rawChannel.id.path)
    )
  }

  private def getBreadcrumb(selfRawChannel: RawChannel): Seq[ApiSectionReference] = {
    val breadcrumbFromRawChannel: Seq[ApiSectionReference] = Seq.empty
    while (selfRawChannel.parent.isDefined) {
      val currentParent: RawChannel = selfRawChannel.parent.get
      breadcrumbFromRawChannel :+ ApiSectionReference(
        label = Some(currentParent.id.label),
        path = Some(currentParent.id.path)
      )
    }
    breadcrumbFromRawChannel
  }

  /** Converter method that takes a rawChannel and returns an ApiConfiguration from its data
    *
    * @param rawChannel the rawChannel produced by ConfigMcConfigface
    * @return a new ApiConfiguration Object with the data from the rawChannel
    */
  def apiConfiguationFromRawChannelConfiguration(rawChannel: RawChannel): ApiConfiguration = {
    val maybeHeader: Option[RawChannelHeader] = rawChannel.config.flatMap(_.header)

    val breadcrumb: Seq[ApiSectionReference] = getBreadcrumb(rawChannel)



    ApiConfiguration(
      meta = apiMetaConfigurationFromRawChannel(rawChannel),
      commercial = Some(apiCommercialConfigurationFromRawChannel(rawChannel)),
      branding = None,
      header = None,
      theme = None
    )
  }

  private def unwrappedDefinesAdTag(rawChannel: RawChannel) = rawChannel.config.flatMap(_.commercial).exists(_.unwrappedDefinesAdTag)
  private def unwrappedDefinesVideoAdTag(rawChannel: RawChannel) = rawChannel.config.flatMap(_.commercial).exists(_.unwrappedDefinesVideoAdTag)

  private def calculateAdTech(rawChannel: RawChannel): String = {
    var currentChannel = rawChannel
    while (!unwrappedDefinesAdTag(currentChannel) && currentChannel.parent.isDefined && currentChannel.id.path != "/") {
      currentChannel = currentChannel.parent.get
    }
    currentChannel.id.path.replaceAll("/", "")
  }

  private def calculateVideoAdTech(rawChannel: RawChannel): String = {
    var currentChannel = rawChannel
    while (!unwrappedDefinesVideoAdTag(currentChannel) && currentChannel.parent.isDefined && currentChannel.id.path != "/") {
      currentChannel = currentChannel.parent.get
    }
    currentChannel.id.path.replaceAll("/", "")
  }

  private def apiMetaConfigurationFromRawChannel(rawChannel: RawChannel) = {
    rawChannel.config.flatMap(_.metaTags).map(metaTags => ApiMetaConfiguration(title = metaTags.title,
      description = metaTags.title,
      tags = metaTags.keywords))
  }

  private def apiCommercialConfigurationFromRawChannel(rawChannel: RawChannel) = {
    ApiCommercialConfiguration(
      adTag = Some(calculateAdTech(rawChannel)),
      videoAdTag = Some(calculateVideoAdTech(rawChannel))
    )
  }

  private def apiBrandingConfigurationFromRawChannel(rawChannel: RawChannel) = {

  }

}

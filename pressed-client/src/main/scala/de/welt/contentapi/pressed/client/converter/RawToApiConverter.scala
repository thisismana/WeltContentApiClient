package de.welt.contentapi.pressed.client.converter

import de.welt.contentapi.core.models.ApiSectionReference
import de.welt.contentapi.pressed.models.{ApiChannel, ApiCommercialConfiguration, ApiConfiguration, ApiHeaderConfiguration, ApiMetaConfiguration, ApiSponsoringConfiguration, ApiThemeConfiguration}
import de.welt.contentapi.raw.models.{RawChannel, RawSectionReference}

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
  def apiConfiguationFromRawChannelConfiguration(rawChannel: RawChannel) = ApiConfiguration(
    meta = apiMetaConfigurationFromRawChannel(rawChannel),
    commercial = Some(apiCommercialConfigurationFromRawChannel(rawChannel)),
    sponsoring = Some(apiSponsoringConfigurationFromRawChannel(rawChannel)),
    header = Some(apiHeaderConfigurationFromRawChannel(rawChannel)),
    theme = Some(apiThemeFromRawChannel(rawChannel))
  )

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

  private def apiSponsoringConfigurationFromRawChannel(rawChannel: RawChannel) = {
    ApiSponsoringConfiguration(
      rawChannel.config.flatMap(_.header.flatMap(_.sponsoring))
    )
  }

  private def apiHeaderConfigurationFromRawChannel(rawChannel: RawChannel) = {
    val apiSectionRefenreces = apiSectionReferencesFromRawSectionReferences(
      rawChannel.config.flatMap(_.header).flatMap(_.references).map(_.toSeq).getOrElse(Nil)
    )
    ApiHeaderConfiguration(
      title = rawChannel.config.flatMap(_.header).flatMap(_.label),
      sectionReferences = Some(apiSectionRefenreces)
    )
  }


  private def apiSectionReferencesFromRawSectionReferences(references: Seq[RawSectionReference]) = {
    references.map(ref => ApiSectionReference(ref.label, ref.path))
  }

  // TODO: find right way to find theme
  // e.g. one for
  // - /mediathek/**
  // - /icon/
  // or persisted in rawChannel ?
  private def apiThemeFromRawChannel(rawChannel: RawChannel) = {
    ApiThemeConfiguration(name = Some("default"))
  }

}

package de.welt.contentapi.pressed.models

import play.api.libs.json._

object PressedFormats {

  import de.welt.contentapi.core.models.ApiFormats._

  implicit val apiTeaserConfigFormat: Format[ApiTeaserConfig] = Json.format[ApiTeaserConfig]
  implicit val apiTeaserFormat: Format[ApiTeaser] = Json.format[ApiTeaser]
  implicit val apiChannelFormat: Format[ApiChannel] = Json.format[ApiChannel]
  implicit val apiStageConfigurationFormat: Format[ApiStageConfiguration] = Json.format[ApiStageConfiguration]
  implicit val apiStageFormat: Format[ApiStage] = Json.format[ApiStage]
  implicit val apiCommercialConfigurationFormat: Format[ApiCommercialConfiguration] = Json.format[ApiCommercialConfiguration]
  implicit val apiThemeConfigurationFormat: Format[ApiThemeConfiguration] = Json.format[ApiThemeConfiguration]
  implicit val apiHeaderConfigurationFormat: Format[ApiHeaderConfiguration] = Json.format[ApiHeaderConfiguration]
  implicit val apiBrandingConfigurationFormat: Format[ApiSponsoringConfiguration] = Json.format[ApiSponsoringConfiguration]
  implicit val apiMetaConfigurationFormat: Format[ApiMetaConfiguration] = Json.format[ApiMetaConfiguration]
  implicit val apiConfigurationFormat: Format[ApiConfiguration] = Json.format[ApiConfiguration]
  implicit val apiPressedContentFormat: Format[ApiPressedContent] = Json.format[ApiPressedContent]
  implicit val apiPressedSectionFormat: Format[ApiPressedSection] = Json.format[ApiPressedSection]
}

object PressedReads {

  import de.welt.contentapi.core.models.ApiReads._

  implicit val apiTeaserConfigReads: Reads[ApiTeaserConfig] = Json.reads[ApiTeaserConfig]
  implicit val apiTeaserReads: Reads[ApiTeaser] = Json.reads[ApiTeaser]
  implicit val apiChannelReads: Reads[ApiChannel] = Json.reads[ApiChannel]
  implicit val apiStageConfigurationReads: Reads[ApiStageConfiguration] = Json.reads[ApiStageConfiguration]
  implicit val apiStageReads: Reads[ApiStage] = Json.reads[ApiStage]
  implicit val apiCommercialConfigurationReads: Reads[ApiCommercialConfiguration] = Json.reads[ApiCommercialConfiguration]
  implicit val apiThemeConfigurationReads: Reads[ApiThemeConfiguration] = Json.reads[ApiThemeConfiguration]
  implicit val apiHeaderConfigurationReads: Reads[ApiHeaderConfiguration] = Json.reads[ApiHeaderConfiguration]
  implicit val apiBrandingConfigurationReads: Reads[ApiSponsoringConfiguration] = Json.reads[ApiSponsoringConfiguration]
  implicit val apiMetaConfigurationReads: Reads[ApiMetaConfiguration] = Json.reads[ApiMetaConfiguration]
  implicit val apiConfigurationReads: Reads[ApiConfiguration] = Json.reads[ApiConfiguration]
  implicit val apiPressedContentReads: Reads[ApiPressedContent] = Json.reads[ApiPressedContent]
  implicit val apiPressedSectionReads: Reads[ApiPressedSection] = Json.reads[ApiPressedSection]
}

object PressedWrites {

  import de.welt.contentapi.core.models.ApiWrites._

  implicit val apiTeaserConfigWrites: Writes[ApiTeaserConfig] = Json.writes[ApiTeaserConfig]
  implicit val apiTeaserWrites: Writes[ApiTeaser] = Json.writes[ApiTeaser]
  implicit val apiChannelWrites: Writes[ApiChannel] = Json.writes[ApiChannel]
  implicit val apiStageConfigurationWrites: Writes[ApiStageConfiguration] = Json.writes[ApiStageConfiguration]
  implicit val apiStageWrites: Writes[ApiStage] = Json.writes[ApiStage]
  implicit val apiCommercialConfigurationWrites: Writes[ApiCommercialConfiguration] = Json.writes[ApiCommercialConfiguration]
  implicit val apiThemeConfigurationWrites: Writes[ApiThemeConfiguration] = Json.writes[ApiThemeConfiguration]
  implicit val apiHeaderConfigurationWrites: Writes[ApiHeaderConfiguration] = Json.writes[ApiHeaderConfiguration]
  implicit val apiBrandingConfigurationWrites: Writes[ApiSponsoringConfiguration] = Json.writes[ApiSponsoringConfiguration]
  implicit val apiMetaConfigurationWrites: Writes[ApiMetaConfiguration] = Json.writes[ApiMetaConfiguration]
  implicit val apiConfigurationWrites: Writes[ApiConfiguration] = Json.writes[ApiConfiguration]
  implicit val apiPressedContentWrites: Writes[ApiPressedContent] = Json.writes[ApiPressedContent]
  implicit val apiPressedSectionWrites: Writes[ApiPressedSection] = Json.writes[ApiPressedSection]
}

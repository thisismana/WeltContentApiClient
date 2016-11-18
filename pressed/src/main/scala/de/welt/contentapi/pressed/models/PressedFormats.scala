package de.welt.contentapi.pressed.models

import play.api.libs.json.{Format, Json, Reads, Writes}

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
}

object PressedReads {

  import de.welt.contentapi.core.models.ApiReads._

  implicit val apiTeaserConfigReads = Json.reads[ApiTeaserConfig]
  implicit val apiTeaserReads = Json.reads[ApiTeaser]
  implicit val apiChannelReads: Reads[ApiChannel] = Json.reads[ApiChannel]
  implicit val apiStageConfigurationReads = Json.reads[ApiStageConfiguration]
  implicit val apiStageReads = Json.reads[ApiStage]
  implicit val apiCommercialConfigurationReads: Reads[ApiCommercialConfiguration] = Json.reads[ApiCommercialConfiguration]
  implicit val apiThemeConfigurationReads: Reads[ApiThemeConfiguration] = Json.reads[ApiThemeConfiguration]
  implicit val apiHeaderConfigurationReads: Reads[ApiHeaderConfiguration] = Json.reads[ApiHeaderConfiguration]
  implicit val apiBrandingConfigurationReads: Reads[ApiSponsoringConfiguration] = Json.reads[ApiSponsoringConfiguration]
  implicit val apiMetaConfigurationReads: Reads[ApiMetaConfiguration] = Json.reads[ApiMetaConfiguration]
  implicit val apiConfigurationReads: Reads[ApiConfiguration] = Json.reads[ApiConfiguration]
}

object PressedWrites {

  import de.welt.contentapi.core.models.ApiWrites._

  implicit val apiTeaserConfigWrites = Json.writes[ApiTeaserConfig]
  implicit val apiTeaserWrites = Json.writes[ApiTeaser]
  implicit val apiChannelWrites: Writes[ApiChannel] = Json.writes[ApiChannel]
  implicit val apiStageConfigurationWrites = Json.writes[ApiStageConfiguration]
  implicit val apiStageWrites = Json.writes[ApiStage]
  implicit val apiCommercialConfigurationWrites: Writes[ApiCommercialConfiguration] = Json.writes[ApiCommercialConfiguration]
  implicit val apiThemeConfigurationWrites: Writes[ApiThemeConfiguration] = Json.writes[ApiThemeConfiguration]
  implicit val apiHeaderConfigurationWrites: Writes[ApiHeaderConfiguration] = Json.writes[ApiHeaderConfiguration]
  implicit val apiBrandingConfigurationWrites: Writes[ApiSponsoringConfiguration] = Json.writes[ApiSponsoringConfiguration]
  implicit val apiMetaConfigurationWrites: Writes[ApiMetaConfiguration] = Json.writes[ApiMetaConfiguration]
  implicit val apiConfigurationWrites: Writes[ApiConfiguration] = Json.writes[ApiConfiguration]
}

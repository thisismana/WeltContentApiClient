package de.welt.contentapi.core.models.pressed

import de.welt.contentapi.core.models.content.ApiSectionReference
import play.api.libs.json.{Format, Json, Reads, Writes}

// TODO: ApiSectionReference maybe a content model?

/**
  * Import these into your scope to easily transform Json to the required object.
  * INFO: the order of the reads is very important.
  * see: http://stackoverflow.com/q/26086815/
  */
object ApiConfigurationFormats {
  implicit lazy val apiSectionReferenceFormat: Format[ApiSectionReference] = Json.format[ApiSectionReference]

  implicit lazy val apiThemeConfigurationFormat: Format[ApiThemeConfiguration] = Json.format[ApiThemeConfiguration]
  implicit lazy val apiHeaderConfigurationFormat: Format[ApiHeaderConfiguration] = Json.format[ApiHeaderConfiguration]
  implicit lazy val apiBrandingConfigurationFormat: Format[ApiBrandingConfiguration] = Json.format[ApiBrandingConfiguration]
  implicit lazy val apiCommercialConfigurationFormat: Format[ApiCommercialConfiguration] = Json.format[ApiCommercialConfiguration]
  implicit lazy val apiMetaConfigurationFormat: Format[ApiMetaConfiguration] = Json.format[ApiMetaConfiguration]
  implicit lazy val apiConfigurationFormat: Format[ApiConfiguration] = Json.format[ApiConfiguration]
}

object ApiConfigurationReads {
  implicit lazy val apiSectionReferenceReads: Reads[ApiSectionReference] = Json.reads[ApiSectionReference]

  implicit lazy val apiThemeConfigurationReads: Reads[ApiThemeConfiguration] = Json.reads[ApiThemeConfiguration]
  implicit lazy val apiHeaderConfigurationReads: Reads[ApiHeaderConfiguration] = Json.reads[ApiHeaderConfiguration]
  implicit lazy val apiBrandingConfigurationReads: Reads[ApiBrandingConfiguration] = Json.reads[ApiBrandingConfiguration]
  implicit lazy val apiCommercialConfigurationReads: Reads[ApiCommercialConfiguration] = Json.reads[ApiCommercialConfiguration]
  implicit lazy val apiMetaConfigurationReads: Reads[ApiMetaConfiguration] = Json.reads[ApiMetaConfiguration]
  implicit lazy val apiConfigurationReads: Reads[ApiConfiguration] = Json.reads[ApiConfiguration]
}

object ApiConfigurationWrites {
  implicit lazy val apiSectionReferenceWrites: Writes[ApiSectionReference] = Json.writes[ApiSectionReference]

  implicit lazy val apiThemeConfigurationWrites: Writes[ApiThemeConfiguration] = Json.writes[ApiThemeConfiguration]
  implicit lazy val apiHeaderConfigurationWrites: Writes[ApiHeaderConfiguration] = Json.writes[ApiHeaderConfiguration]
  implicit lazy val apiBrandingConfigurationWrites: Writes[ApiBrandingConfiguration] = Json.writes[ApiBrandingConfiguration]
  implicit lazy val apiCommercialConfigurationWrites: Writes[ApiCommercialConfiguration] = Json.writes[ApiCommercialConfiguration]
  implicit lazy val apiMetaConfigurationWrites: Writes[ApiMetaConfiguration] = Json.writes[ApiMetaConfiguration]
  implicit lazy val apiConfigurationWrites: Writes[ApiConfiguration] = Json.writes[ApiConfiguration]
}

package de.welt.contentapi.raw.models

import play.api.libs.json.{Format, Json, Reads, Writes}

object RawFormats {
  implicit lazy val rawChannelIdFormat: Format[RawChannelId] = Json.format[RawChannelId]
  implicit lazy val rawChannelMetaRobotsTagFormat: Format[RawChannelMetaRobotsTag] = Json.format[RawChannelMetaRobotsTag]
  implicit lazy val rawChannelMetadataFormat: Format[RawChannelMetadata] = Json.format[RawChannelMetadata]
  implicit lazy val rawSectionReferenceFormat: Format[RawSectionReference] = Json.format[RawSectionReference]
  implicit lazy val rawChannelHeaderFormat: Format[RawChannelHeader] = Json.format[RawChannelHeader]
  implicit lazy val rawChannelCommercialFormat: Format[RawChannelCommercial] = Json.format[RawChannelCommercial]
  
  implicit lazy val rawChannelStageContentFormat: Format[RawChannelStageContent] = Json.format[RawChannelStageContent]
  implicit lazy val rawChannelStageModuleFormat: Format[RawChannelStageModule] = Json.format[RawChannelStageModule]
  implicit lazy val rawChannelStageCommercialFormat: Format[RawChannelStageCommercial] = Json.format[RawChannelStageCommercial]
  implicit lazy val rawChannelStageFormat: Format[RawChannelStage] = Json.format[RawChannelStage]
  
  implicit lazy val rawMetadataFormat: Format[RawMetadata] = Json.format[RawMetadata]
  implicit lazy val rawChannelConfigurationFormat: Format[RawChannelConfiguration] = Json.format[RawChannelConfiguration]
  implicit lazy val rawChannelFormat: Format[RawChannel] = Json.format[RawChannel]
}

object RawReads {

  implicit lazy val rawChannelIdReads: Reads[RawChannelId] = Json.reads[RawChannelId]
  implicit lazy val rawChannelMetaRobotsTagReads: Reads[RawChannelMetaRobotsTag] = Json.reads[RawChannelMetaRobotsTag]
  implicit lazy val rawChannelMetadataReads: Reads[RawChannelMetadata] = Json.reads[RawChannelMetadata]
  implicit lazy val rawSectionReferenceReads: Reads[RawSectionReference] = Json.reads[RawSectionReference]
  implicit lazy val rawChannelHeaderReads: Reads[RawChannelHeader] = Json.reads[RawChannelHeader]
  implicit lazy val rawChannelCommercialReads: Reads[RawChannelCommercial] = Json.reads[RawChannelCommercial]

  implicit lazy val rawChannelStageContentReads: Reads[RawChannelStageContent] = Json.reads[RawChannelStageContent]
  implicit lazy val rawChannelStageModuleReads: Reads[RawChannelStageModule] = Json.reads[RawChannelStageModule]
  implicit lazy val rawChannelStageCommercialReads: Reads[RawChannelStageCommercial] = Json.reads[RawChannelStageCommercial]
  implicit lazy val rawChannelStageReads: Reads[RawChannelStage] = Json.reads[RawChannelStage]

  implicit lazy val rawMetadataReads: Reads[RawMetadata] = Json.reads[RawMetadata]
  implicit lazy val rawChannelConfigurationReads: Reads[RawChannelConfiguration] = Json.reads[RawChannelConfiguration]
  implicit lazy val rawChannelReads: Reads[RawChannel] = Json.reads[RawChannel]

}

object RawWrites {

  implicit lazy val rawChannelIdWrites: Writes[RawChannelId] = Json.writes[RawChannelId]
  implicit lazy val rawChannelMetaRobotsTagWrites: Writes[RawChannelMetaRobotsTag] = Json.writes[RawChannelMetaRobotsTag]
  implicit lazy val rawChannelMetadataWrites: Writes[RawChannelMetadata] = Json.writes[RawChannelMetadata]
  implicit lazy val rawSectionReferenceWrites: Writes[RawSectionReference] = Json.writes[RawSectionReference]
  implicit lazy val rawChannelHeaderWrites: Writes[RawChannelHeader] = Json.writes[RawChannelHeader]
  implicit lazy val rawChannelCommercialWrites: Writes[RawChannelCommercial] = Json.writes[RawChannelCommercial]

  implicit lazy val rawChannelStageContentWrites: Writes[RawChannelStageContent] = Json.writes[RawChannelStageContent]
  implicit lazy val rawChannelStageModuleWrites: Writes[RawChannelStageModule] = Json.writes[RawChannelStageModule]
  implicit lazy val rawChannelStageCommercialWrites: Writes[RawChannelStageCommercial] = Json.writes[RawChannelStageCommercial]
  implicit lazy val rawChannelStageWrites: Writes[RawChannelStage] = Json.writes[RawChannelStage]

  implicit lazy val rawMetadataWrites: Writes[RawMetadata] = Json.writes[RawMetadata]
  implicit lazy val rawChannelConfigurationWrites: Writes[RawChannelConfiguration] = Json.writes[RawChannelConfiguration]
  implicit lazy val rawChannelWrites: Writes[RawChannel] = Json.writes[RawChannel]

}

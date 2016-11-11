package de.welt.contentapi.core.models.content2

import de.welt.contentapi.core.models.configuration2.ApiChannel
import de.welt.contentapi.core.models.configuration2.formats.reads.FullChannelReads
import de.welt.contentapi.core.models.configuration2.formats.writes.FullChannelWrites

case class EnrichedApiContent(content: ApiContent, sectionData: Option[SectionData]) {
  def `type`: String = content.`type`
}

case class EnrichedApiResponse(main: EnrichedApiContent, related: List[EnrichedApiContent] = Nil) {

  def onward = related.filter(_.content.unwrappedRoles.contains("onward"))

  def playlist = related.filter(_.content.unwrappedRoles.contains("playlist"))
}

case class SectionData(home: ApiChannel, breadcrumb: Seq[ApiChannel])

object SectionData {
  def fromChannel(channel: ApiChannel) = {
    SectionData(channel.copy(hasChildren = false, children = Seq.empty), channel.getBreadcrumb())
  }
}


object SectionDataFormats {
  import play.api.libs.json._
  implicit val channelFormatFullChildren: Format[ApiChannel] = Format(FullChannelReads.channelReads, FullChannelWrites.channelWrites)

  implicit lazy val sectionDataFormat: Format[SectionData] = Json.format[SectionData]
  implicit lazy val EnrichedApiContentFormat: Format[EnrichedApiContent] = Json.format[EnrichedApiContent]
}

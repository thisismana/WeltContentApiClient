package de.welt.contentapi.legacy.models

import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.pressed.models.ApiPressedContent

case class ApiLegacyLists(lists: Option[Seq[ApiLegacySection]] = None) {
  lazy val unwrappedLists: Seq[ApiLegacySection] = lists.getOrElse(Nil)
}

case class ApiLegacySection(id: String,
                            label: Option[String] = None,
                            content: Option[Seq[ApiContent]] = None) {
  lazy val unwrappedContent: Seq[ApiContent] = content.getOrElse(Nil)
}

case class ApiLegacyPressedSection(id: String,
                                   label: Option[String] = None,
                                   content: Option[Seq[ApiPressedContent]] = None) {
  lazy val unwrappedContent: Seq[ApiPressedContent] = content.getOrElse(Nil)
}

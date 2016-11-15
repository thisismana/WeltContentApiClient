package de.welt.contentapi.core.models.channel

import de.welt.contentapi.core.models.content.ApiSectionReference

case class ApiChannel(section: Option[ApiSectionReference] = None, breadcrumb: Option[Seq[ApiSectionReference]] = None) {
  lazy val unwrappedBreadcrumb: Seq[ApiSectionReference] = breadcrumb.getOrElse(Nil)
}

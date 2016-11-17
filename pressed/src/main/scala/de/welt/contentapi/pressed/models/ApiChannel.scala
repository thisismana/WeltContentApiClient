package de.welt.contentapi.pressed.models

import de.welt.contentapi.core.models.content.ApiSectionReference

private[pressed] case class ApiChannel(section: Option[ApiSectionReference] = None, breadcrumb: Option[Seq[ApiSectionReference]] = None) {
  lazy val unwrappedBreadcrumb: Seq[ApiSectionReference] = breadcrumb.getOrElse(Nil)
}

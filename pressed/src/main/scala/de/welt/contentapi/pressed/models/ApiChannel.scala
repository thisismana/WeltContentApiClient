package de.welt.contentapi.pressed.models

import de.welt.contentapi.core.models.ApiSectionReference

/** ApiChannel is a simple frontend view on 'section data' with the section itself and its parents
  *
  * @param section the current section itself as ApiSectionReference
  * @param breadcrumb the current section and all parents until the 'frontpage' aka '/' aka 'home' as a Seq of ApiSectionReferences
  */
private[pressed] case class ApiChannel(section: Option[ApiSectionReference] = None, breadcrumb: Option[Seq[ApiSectionReference]] = None) {
  lazy val unwrappedBreadcrumb: Seq[ApiSectionReference] = breadcrumb.getOrElse(Nil)
}

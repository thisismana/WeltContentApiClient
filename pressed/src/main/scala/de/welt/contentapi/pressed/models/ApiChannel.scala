package de.welt.contentapi.pressed.models

import de.welt.contentapi.core.models.ApiReference

/**
  * [[ApiChannel]] is a simplified view on a channel.
  * The channel has a location (reference), breadcrumb and maybe a master channel.
  *
  * @param section    the current section itself as [[ApiReference]]
  * @param master     the calculated master for the section.
  *                   Beware Naming: A master channel is _not_ the root, home or parent channel.
  *                   Think of it as a "Heimat"-Section. Examples:
  *                   - (maybe) the master channel of '/icon/uhren/' is '/icon/'
  *                   - (maybe) the master channel of '/sport/fussball/bundesliga/FooBar/' is '/sport/fussball/'
  * @param breadcrumb the current section and all parents ending at the 'frontpage' aka '/' aka 'home'
  *                   as a Seq[[ApiReference]].
  * @param brand      flags this channel as a 'brand'. A brand is a "Sub-Marke" like Icon ('/icon/') with
  *                   different UI elements or layouts.
  */
case class ApiChannel(section: Option[ApiReference] = None,
                      master: Option[ApiReference] = None,
                      breadcrumb: Option[Seq[ApiReference]] = None,
                      brand: Option[Boolean] = None) {
  lazy val unwrappedBreadcrumb: Seq[ApiReference] = breadcrumb.getOrElse(Nil)
  lazy val isBrand: Boolean = brand.getOrElse(false)
}

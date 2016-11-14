package de.welt.contentapi.core.models.internal.layouts

import de.welt.contentapi.core.models.configuration2.formats.Datasource
import de.welt.contentapi.core.models.section2.ApiCommercial

case class LayoutTheme(name: String = "",
                       defaultContentSource: Option[Datasource] = None,
                       teaserProfileInOrder: Seq[String] = Nil,
                       teaserTypesInOrder: Seq[String] = Nil,
                       commercials: Option[Seq[ApiCommercial]] = None
                      )

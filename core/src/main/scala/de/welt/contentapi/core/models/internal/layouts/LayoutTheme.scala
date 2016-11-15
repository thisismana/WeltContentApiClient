package de.welt.contentapi.core.models.internal.layouts

import de.welt.contentapi.core.models.section.commercial.ApiCommercial


case class    LayoutTheme(name: String = "",
//                       defaultContentSource: Option[Datasource] = None,
                       teaserProfileInOrder: Seq[String] = Nil,
                       teaserTypesInOrder: Seq[String] = Nil,
                       commercials: Option[Seq[ApiCommercial]] = None
                      )

package de.welt.contentapi.core.models.section.stage

import de.welt.contentapi.core.models.configuration.formats.Datasource.SearchSource
import de.welt.contentapi.core.models.section2.ApiCommercial

case class Layout(name: String = "",
                   defaultContentSource: Option[SearchSource] = None,
                   teaserProfileInOrder: Seq[String] = Nil,
                   teaserTypesInOrder: Seq[String] = Nil,
                   commercials: Option[Seq[ApiCommercial]] = None
                 )

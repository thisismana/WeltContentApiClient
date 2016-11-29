package de.welt.contentapi.pressed.models

import de.welt.contentapi.core.models.ApiReference


case class ApiStage(teasers: Seq[ApiTeaser],
                    configuration: Option[ApiStageConfiguration] = None)

case class ApiStageConfiguration(layout: String,
                                 label: Option[String],
                                 sectionReferences: Option[Seq[ApiReference]] = None,
                                 commercials: Option[Seq[String]] = None) {

  lazy val getUnwrappedCommercials = commercials.getOrElse(Nil)
  lazy val unwrappedSectionReferences: Seq[ApiReference] = sectionReferences.getOrElse(Seq.empty)
}


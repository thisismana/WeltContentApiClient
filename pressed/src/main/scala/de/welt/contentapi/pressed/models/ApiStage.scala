package de.welt.contentapi.pressed.models

import de.welt.contentapi.core.models.ApiReference

/**
  * todo harry: add docs
  * @param teasers
  * @param configuration
  */
case class ApiStage(teasers: Seq[ApiTeaser],
                    configuration: Option[ApiStageConfiguration] = None)

/**
  * todo harry: add docs
  * @param layout
  * @param hidden `true` == stage should not be rendered -- even with teasers
  * @param label
  * @param sectionReferences
  * @param commercials
  */
case class ApiStageConfiguration(layout: String,
                                 hidden: Boolean,
                                 label: Option[String],
                                 sectionReferences: Option[Seq[ApiReference]] = None,
                                 commercials: Option[Seq[String]] = None) {
  lazy val unwrappedCommercials: Seq[String] = commercials.getOrElse(Nil)
  lazy val unwrappedSectionReferences: Seq[ApiReference] = sectionReferences.getOrElse(Nil)
}


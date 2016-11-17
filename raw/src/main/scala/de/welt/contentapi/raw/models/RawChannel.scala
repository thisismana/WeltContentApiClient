package de.welt.contentapi.raw.models

import java.time.Instant

case class RawChannel(id: RawChannelId,
                      config: RawChannelConfiguration,
                      metadata: Option[Metadata] = None,
                      stages: Option[Seq[RawChannelStage]] = None) {
  lazy val unwrappedStages: Seq[RawChannelStage] = stages.getOrElse(Nil)
}

case class RawChannelId(path: String,
                        label: String,
                        escenicId: Long = -1)

case class RawChannelConfiguration(metaTags: Option[RawChannelMetaTags] = None,
                                   header: Option[RawChannelHeader] = None,
                                   commercial: Option[RawChannelAdData] = None)

case class RawChannelAdData(definesAdTag: Option[Boolean] = None,
                            definesVideoAdTag: Option[Boolean] = None) {
  lazy val unwrappedDefinesAdTag: Boolean = definesAdTag.getOrElse(false)
  lazy val unwrappedDefinesVideoAdTag: Boolean = definesVideoAdTag.getOrElse(false)
}

case class RawChannelMetaTags(title: Option[String] = None,
                              description: Option[String] = None,
                              keywords: Option[String] = None,
                              contentRobots: Option[RawChannelMetaRobotsTag] = None,
                              sectionRobots: Option[RawChannelMetaRobotsTag] = None)

case class RawChannelMetaRobotsTag(noIndex: Option[String] = None, noFollow: Option[String] = None)

case class RawSectionReference(label: Option[String] = None, path: Option[String] = None)

// Logo/Header Prio: Ressort Icon > Channel Icon > RawChannelId Label
case class RawChannelHeader(sponsoring: Option[String], // like 'tagheuer'
                            logo: Option[String], // could be Channel logo (e.g. /icon) or Ressort logo (e.g. /kmpkt)
                            slogan: Option[String], // belongs to the logo
                            label: Option[String]) // replaced by logo if set


/**
  * @param changedBy        id of last sitebuilder
  * @param lastModifiedDate timestamp of last change
  * @param modified         was this channel configured via ConfigMcConfigface or is it still like `default`
  * @param isRessort        so far /icon, maybe blau and bilanz will be added (used for tree logic in angular app)
  */
case class Metadata(changedBy: String = "system",
                    lastModifiedDate: Long = Instant.now.toEpochMilli,
                    modified: Boolean = false,
                    isRessort: Boolean = false)


trait RawChannelStage {
  val `type`: String
  val index: Int
  val label: String
  val references: Option[Seq[RawSectionReference]]
  val teaserLimit: Option[Int]

  def unwrappedReferences: Seq[RawSectionReference] = references.getOrElse(Nil)
}

case class RawChannelStageContent(index: Int,
                                  label: String,
                                  references: Option[Seq[RawSectionReference]],
                                  sourceOverride: Option[String],
                                  teaserLimit: Option[Int],
                                  desktopLayout: Option[String]) extends RawChannelStage {
  override val `type`: String = "content"
}

case class RawChannelStageModule(index: Int,
                                 label: String,
                                 references: Option[Seq[RawSectionReference]],
                                 modul: String,
                                 teaserLimit: Option[Int]) extends RawChannelStage {
  override val `type`: String = "module"
}

case class RawChannelStageCommercial(index: Int,
                                     label: String,
                                     references: Option[Seq[RawSectionReference]],
                                     commercial: Option[String]) extends RawChannelStage {
  override val `type`: String = "commercial"
  override val teaserLimit: Option[Int] = None
}


package de.welt.contentapi.core.models.configface

import java.time.Instant

case class RawChannel(id: RawChannelId,
                      metadata: Metadata,
                      config: RawChannelConfiguration,
                      stages: Option[Seq[RawChannelStage]]) {
}

case class RawChannelId(path: String,
                        label: String,
                        ece: Long = -1)

case class RawChannelConfiguration(metaTags: Option[RawChannelMetaTags] = None,
                                   branding: Option[String] = None,
                                   headerLogo: Option[String] = None
                                  )

case class RawChannelAdData(definesAdTag: Boolean = false,
                            definesVideoAdTag: Option[Boolean] = None
                           ) {
  lazy val unwrappedDefinesVideoAdTag = definesVideoAdTag.getOrElse(false)
}

case class RawChannelMetaTags(title: Option[String] = None,
                              description: Option[String] = None,
                              keywords: Option[List[String]] = None) {
  lazy val unwrappedTags: List[String] = keywords.getOrElse(Nil)
}

case class RawSectionReference(label: Option[String] = None, path: Option[String] = None)

// Logo/Header Prio: Ressort Icon > Channel Icon > RawChannelId Label
case class RawChannelHeader(sponsoring: Option[String], // like 'tagheuer'
                            logo: Option[String], // could be Channel logo (e.g. /icon) or Ressort logo (e.g. /kmpkt)
                            slogan: Option[String], // belongs to the logo
                            label: Option[String] // replaced by logo if set
                           )


/**
  * @param changedBy id of last sitebuilder
  * @param lastModifiedDate timestamp of last change
  * @param modified was this channel configured via ConfigMcConfigface or is it still like `default`
  * @param isRessort so far /icon, maybe blau and bilanz will be added (used for tree logic in angular app)
  */
private[configface] case class Metadata(changedBy: String = "system",
                                        lastModifiedDate: Long = Instant.now.toEpochMilli,
                                        modified: Boolean = false,
                                        isRessort: Boolean = false)


trait RawChannelStage {
  val `type`: String
  val index: Int
  val label: String
  val references: Option[Seq[RawSectionReference]]
  val teaserLimit: Option[Int]

  def unwrappedReferences = references.getOrElse(Nil)
}

case class RawChannelStageContent(index: Int,
                                  label: String,
                                  references: Option[Seq[RawSectionReference]],
                                  sourceOverride: Option[String],
                                  teaserLimit: Option[Int]) extends RawChannelStage {
  override val `type`: String = "content"
}

case class RawChannelStageModul(index: Int,
                                label: String,
                                references: Option[Seq[RawSectionReference]],
                                modul: String,
                                teaserLimit: Option[Int]) extends RawChannelStage {
  override val `type`: String = "modul"
}

case class RawChannelStageCommercial(index: Int,
                                     label: String,
                                     references: Option[Seq[RawSectionReference]],
                                     commercial: Option[String]) extends RawChannelStage {
  override val `type`: String = "commercial"
  override val teaserLimit: Option[Int] = None
}


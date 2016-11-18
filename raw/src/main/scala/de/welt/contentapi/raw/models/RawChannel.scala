package de.welt.contentapi.raw.models

import java.time.Instant

/**
  * Tree structure of the escenic channel/section tree. Simple representation:
  * - '/' root (WON_frontpage)
  * |-- '/sport/'
  *    |-- '/sport/fussball/'
  *    |-- '/sport/formel1/'
  * |-- '/wirtschaft/'
  * |-- '/politik/'
  *
  * @param id mandatory id with the channel path. E.g. /sport/fussball/
  * @param config
  * @param metadata
  * @param stages
  * @param parent the maybe parent of the current channel. Root channel has no parent. (no shit sherlock!)
  * @param children all children of the current channel
  */
case class RawChannel(id: RawChannelId,
                      config: Option[RawChannelConfiguration] = None,
                      stages: Option[Seq[RawChannelStage]] = None,
                      metadata: Option[Metadata] = None,
                      parent: Option[RawChannel] = None,
                      children: Option[Seq[RawChannel]] = None) {
  lazy val unwrappedStages: Seq[RawChannelStage] = stages.getOrElse(Nil)


  def findByPath(search: String): Option[RawChannel] = findByPath(
    search.split('/').filter(_.nonEmpty).toList match {
      case Nil ⇒ Nil
      case head :: tail ⇒ tail.scanLeft(s"/$head/")((path, s) ⇒ path + s + "/")
    }
  )

  private def findByPath(sectionPath: Seq[String]): Option[RawChannel] = {
    sectionPath match {
      case Nil ⇒
        Some(this)
      case head :: Nil ⇒
        children.getOrElse(Nil).find(_.id.path == head)
      case head :: tail ⇒
        children.getOrElse(Nil).find(_.id.path == head).flatMap(_.findByPath(tail))
    }
  }

}

/**
  * @param path unique path of the channel. Always with a trailing slash. E.g. /sport/fussball/
  * @param label label of the channel. This is the display name from escenic. (provided by SDP)
  * @param escenicId escenic id of the section. E.g. root channel ('/') with id 5
  */
case class RawChannelId(path: String,
                        label: String,
                        escenicId: Long = -1)

case class RawChannelConfiguration(metaTags: Option[RawChannelMetaTags] = None,
                                   header: Option[RawChannelHeader] = None,
                                   commercial: Option[RawChannelCommercial] = None)

case class RawChannelCommercial(definesAdTag: Option[Boolean] = None,
                                definesVideoAdTag: Option[Boolean] = None) {
  lazy val unwrappedDefinesAdTag: Boolean = definesAdTag.getOrElse(false)
  lazy val unwrappedDefinesVideoAdTag: Boolean = definesVideoAdTag.getOrElse(false)
}

case class RawChannelMetaTags(title: Option[String] = None,
                              description: Option[String] = None,
                              keywords: Option[Seq[String]] = None,
                              contentRobots: Option[RawChannelMetaRobotsTag] = None,
                              sectionRobots: Option[RawChannelMetaRobotsTag] = None) {
  lazy val unwrappedKeywords: Seq[String] = keywords.getOrElse(Nil)
}

case class RawChannelMetaRobotsTag(noIndex: Option[String] = None, noFollow: Option[String] = None)

case class RawSectionReference(label: Option[String] = None, path: Option[String] = None)

// Logo/Header Prio: Ressort Icon > Channel Icon > RawChannelId Label
case class RawChannelHeader(sponsoring: Option[String] = None, // like 'tagheuer'
                            logo: Option[String] = None, // could be Channel logo (e.g. /icon) or Ressort logo (e.g. /kmpkt)
                            slogan: Option[String] = None, // belongs to the logo
                            label: Option[String] = None) // replaced by logo if set


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
  val references: Option[Seq[RawSectionReference]] = None
  val teaserLimit: Option[Int] = None

  def unwrappedReferences: Seq[RawSectionReference] = references.getOrElse(Nil)
}

case class RawChannelStageContent(index: Int,
                                  label: String,
                                  override val references: Option[Seq[RawSectionReference]] = None,
                                  override val teaserLimit: Option[Int] = None,
                                  sourceOverride: Option[String] = None,
                                  desktopLayout: Option[String] = None) extends RawChannelStage {
  override val `type`: String = "content"
}

case class RawChannelStageModule(index: Int,
                                 label: String,
                                 override val references: Option[Seq[RawSectionReference]] = None,
                                 override val teaserLimit: Option[Int] = None,
                                 module: String) extends RawChannelStage {
  override val `type`: String = "module"
}

case class RawChannelStageCommercial(index: Int,
                                     label: String,
                                     override val references: Option[Seq[RawSectionReference]] = None,
                                     commercial: Option[String] = None) extends RawChannelStage {
  override val `type`: String = "commercial"
  override val teaserLimit: Option[Int] = None
}


package de.welt.contentapi.core.models.section2

case class Stage(id: String = "stageId",
                 index: Int = -1,
                 sources: Option[Seq[Datasource]] = None,
                 config: Option[ApiStageConfig] = None,
                 groups: Option[Seq[ApiStageGroup]] = None) {
  lazy val unwrappedSources: Seq[Datasource] = sources.getOrElse(Nil)
  lazy val unwrappedGroups: Seq[ApiStageGroup] = groups.getOrElse(Nil)
}

/**
  * rowType determines the Grid Layout
  * can be "stageHero" or "default"
  * e.g. StageHero with 2 Articles =>
  * ########|####
  * ########|####
  * ########|####
  * e.g. default with 2 Articles =>
  * ######|######
  * ######|######
  * ######|######
  */
case class ApiStageGroup(rowType: String = "default",
                         teaserType: String = "default")

case class ApiStageConfig(maxSize: Option[Int] = None,
                          stageTheme: Option[ApiStageTheme] = None,
                          headlineTheme: Option[ApiHeadlineTheme] = None,
                          stageType: Option[String] = None,
                          sectionReferences: Option[Seq[ApiSectionReference]] = None,
                          commercials: Option[Seq[ApiCommercial]] = None) {
  lazy val unwrappedSectionReferences: Seq[ApiSectionReference] = sectionReferences.getOrElse(Nil)
  lazy val unwrappedCommercials: Seq[ApiCommercial] = commercials.getOrElse(Nil)
}

case class ApiHeadlineTheme(label: String, small: Boolean = false)

case class ApiCommercial(format: String) {
  import ApiCommercialDefinitions._
  def apply(value: String): ApiCommercial = {
    value match {
      case MEDIUM_RECTANGLE.`format` => MEDIUM_RECTANGLE
      case BILLBOARD2.`format` => BILLBOARD2
      case _ => ApiCommercial(format = value)
    }
  }
}

object ApiCommercialDefinitions {
  lazy val MEDIUM_RECTANGLE = ApiCommercial(format = "MediumRectangle")
  lazy val BILLBOARD2 = ApiCommercial(format = "BillBoard2")
}

case class ApiSectionReference(path: String, label: String)

case class ApiStageTheme(name: String)










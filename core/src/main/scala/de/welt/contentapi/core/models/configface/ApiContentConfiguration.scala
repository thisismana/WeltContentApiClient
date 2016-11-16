package de.welt.contentapi.core.models.configface

case class ApiContentConfiguration(stages: Option[Seq[ApiStageDefinition]] = None) {
  lazy val unwrappedStages: Seq[ApiStageDefinition] = stages.getOrElse(Nil)
}

sealed trait ApiStageDefinition {
  val index: Int
  val `type`: String
}

case class ApiTeaserStageDefinition(index: Int,
                                    title: Option[String] = None,
                                    desktopLayout: Option[String] = None,
                                    sectionSource: Option[String] = None,
                                    maxTeaserCount: Int) extends ApiStageDefinition {
  override val `type`: String = "teaserStage"
}

case class ApiCommercialStageDefinition(index: Int,
                                        commercialType: String) extends ApiStageDefinition {
  override val `type`: String = "commercialStage"
}


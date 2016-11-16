package de.welt.contentapi.core.models.resolve.contentapi


case class ApiContentSearch(`type`: Option[MainTypeParam],
                            subType: Option[SubTypeParam],
                            section: Option[SectionParam],
                            homeSection: Option[HomeSectionParam],
                            sectionExcludes: Option[SectionExcludes],
                            flags: Option[FlagParam],
                            limit: Option[Int]
                           ) {
  def getAllQuerieParameters: Seq[String] = ??? // `type`.map(_.getKeyValue) // TODO
}

sealed trait SearchParams {
  val queryParamName: String
  val queryValue: String
  def getKeyValue = queryParamName + "=" + queryValue
}


case class MainTypeParam(queryValue: String)
  extends SearchParams {
  override val queryParamName: String = "type"
}

case class SubTypeParam(queryValue: String)
  extends SearchParams {
  override val queryParamName: String = "subType"
}

case class SectionParam(queryValue: String)
  extends SearchParams {
  override val queryParamName: String = "sectionPath"
}

case class HomeSectionParam(queryValue: String)
  extends SearchParams {
  override val queryParamName: String = "sectionHome"
}

case class SectionExcludes(queryValue: String)
  extends SearchParams {
  override val queryParamName: String = "excludeSections"
}

case class FlagParam(queryValue: String)
  extends SearchParams {
  override val queryParamName: String = "flag"
}


sealed trait Datasource {
  def `type`: String
  def maxSize: Option[Int]
}

case class CuratedSource(override val maxSize: Option[Int],
                         papyrusFolder: String,
                         papyrusFile: String
                        ) extends Datasource {
  override val `type`: String = "curated"
}

case class SearchSource(override val maxSize: Option[Int],
                        queries: Seq[SearchParams] = Seq()) extends Datasource {
  override val `type`: String = "search"
}


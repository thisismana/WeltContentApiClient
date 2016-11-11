import de.welt.contentapi.core.models.configuration.formats.{Datasource, Query}
import de.welt.contentapi.core.models.configuration.formats.Datasource._
import de.welt.contentapi.core.models.configuration.formats.Query._
import de.welt.contentapi.core.models.section2._

object formats {
  import play.api.libs.json._

  // need typesafe val, because default Type is OFormat[...]
  implicit lazy val commercialFormat: Format[ApiCommercial] = Json.format[ApiCommercial]
  implicit lazy val sectionReferenceFormat: Format[ApiSectionReference] = Json.format[ApiSectionReference]
  implicit lazy val headlineThemeFormat: Format[ApiHeadlineTheme] = Json.format[ApiHeadlineTheme]
  implicit lazy val stageThemeFormat: Format[ApiStageTheme] = Json.format[ApiStageTheme]
  implicit lazy val stageConfigFormat: Format[ApiStageConfig] = Json.format[ApiStageConfig]
  implicit lazy val stageGroupFormat: Format[ApiStageGroup] = Json.format[ApiStageGroup]
  implicit lazy val stageFormat: Format[Stage] = Json.format[Stage]
  // Data Sources
  implicit lazy val datasourceFormat: Format[Datasource] = Json.format[Datasource]
  implicit lazy val searchSourceFormat: Format[SearchSource] = Json.format[SearchSource]
  implicit lazy val curatedSourceFormat: Format[CuratedSource] = Json.format[CuratedSource]
  // SearchApiFilters
  implicit lazy val queryFormat: Format[Query] = Json.format[Query]
  implicit lazy val flagQueryFormat: Format[FlagQuery] = Json.format[FlagQuery]
  implicit lazy val sectionQueryFormat: Format[SectionQuery] = Json.format[SectionQuery]
  implicit lazy val subtypeQueryFormat: Format[SubTypeQuery] = Json.format[SubTypeQuery]
  implicit lazy val typeQueryFormat: Format[TypeQuery] = Json.format[TypeQuery]
}
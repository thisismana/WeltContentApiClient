package de.welt.contentapi.core.models

object pressed {

  case class SectionPage(config: SectionPageConfig, stages: Seq[ContentStage] = Seq.empty)

  case class SectionPageConfig(displayName: String,
                               adZone: String,
                               path: String,
                               theme: SectionPageTheme,
                               keywords: Option[String],
                               description: Option[String],
                               title: Option[String]
                              )

  object SectionPageConfig {
    def fromChannel(channel: Channel) = SectionPageConfig(
      displayName = channel.data.label,
      adZone = channel.getAdTag.getOrElse("home").stripPrefix("/").stripSuffix("/") + "_index",
      path = channel.id.path,
      theme = SectionPageTheme(),
      keywords = channel.data.fields.flatMap(_.get("keywords")),
      description = channel.data.fields.flatMap(_.get("description")),
      title = channel.data.fields.flatMap(_.get("title"))
    )
  }

  case class SectionPageTheme(name: String = "default")

  case class ContentStage(config: StageConfig,
                          content: Seq[PressedContent])

  case class PressedContent(config: PressedContentConfig,
                            content: EnrichedApiContent)

  case class PressedContentConfig(profile: String = "wide")


  object PressedFormats {

    import play.api.libs.json._
    import de.welt.contentapi.core.models.SectionDataFormats._
    import de.welt.contentapi.core.models.ApiFormats._
    import de.welt.contentapi.core.models.StageFormats._

    implicit lazy val pressedContentConfigFormat: Format[PressedContentConfig] = Json.format[PressedContentConfig]
    implicit lazy val pressedContentFormat: Format[PressedContent] = Json.format[PressedContent]
    implicit lazy val sectionReferenceFormat: Format[SectionReference] = Json.format[SectionReference]
    implicit lazy val stageMetadataFormat: Format[StageTheme] = Json.format[StageTheme]
    implicit lazy val contentStageWrites: Format[ContentStage] = Json.format[ContentStage]
    implicit lazy val sectionPageThemeWrites: Format[SectionPageTheme] = Json.format[SectionPageTheme]
    implicit lazy val sectionPageConfigWrites: Format[SectionPageConfig] = Json.format[SectionPageConfig]
    implicit lazy val sectionPageWrites: Format[SectionPage] = Json.format[SectionPage]
  }

}
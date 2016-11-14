import de.welt.contentapi.core.models.section.stage.{Teaser, TeaserConfig}
import play.api.libs.json.Json

object TeaserFormats {
  implicit val teaserFormat = Json.format[Teaser]
  implicit val teaserConfigFormat = Json.format[TeaserConfig]
}
import de.welt.contentapi.core.models.section.commercial.ApiCommercial
import play.api.libs.json.Json

object ApiCommercialFormats {
  implicit val apiCommercialFormat = Json.format[ApiCommercial]
}
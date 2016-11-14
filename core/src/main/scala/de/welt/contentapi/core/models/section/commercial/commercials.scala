package de.welt.contentapi.core.models.section.commercial

import CommercialLibrary._
import play.api.libs.json.Json

case class ApiCommercial(format: String) {
  def apply(value: String): ApiCommercial = {
    value match {
      case MEDIUM_RECTANGLE.`format` => MEDIUM_RECTANGLE
      case BILLBOARD2.`format` => BILLBOARD2
      case _ => ApiCommercial(format = value)
    }
  }
}

object format {
  implicit val apiCommercialFormat = Json.format[ApiCommercial]
}

object CommercialLibrary {
  lazy val MEDIUM_RECTANGLE = ApiCommercial(format = "MediumRectangle")
  lazy val BILLBOARD2 = ApiCommercial(format = "BillBoard2")
}

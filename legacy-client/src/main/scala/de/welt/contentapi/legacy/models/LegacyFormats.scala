package de.welt.contentapi.legacy.models

import play.api.libs.json.{Format, Json, Reads, Writes}

object LegacyFormats {
  import de.welt.contentapi.core.models.ApiFormats._
  import de.welt.contentapi.pressed.models.PressedFormats._

  implicit val legacySectionFormat: Format[ApiLegacySection] = Json.format[ApiLegacySection]
  implicit val legacyPressedSectionFormat: Format[ApiLegacyPressedSection] = Json.format[ApiLegacyPressedSection]
  implicit val legacyListsFormat: Format[ApiLegacyLists] = Json.format[ApiLegacyLists]
}

object LegacyReads {
  import de.welt.contentapi.core.models.ApiReads._
  import de.welt.contentapi.pressed.models.PressedReads._

  implicit val legacySectionReads: Reads[ApiLegacySection] = Json.reads[ApiLegacySection]
  implicit val legacyPressedSectionReads: Reads[ApiLegacyPressedSection] = Json.reads[ApiLegacyPressedSection]
  implicit val legacyListsReads: Reads[ApiLegacyLists] = Json.reads[ApiLegacyLists]
}

object LegacyWrites {
  import de.welt.contentapi.core.models.ApiWrites._
  import de.welt.contentapi.pressed.models.PressedWrites._

  implicit lazy val legacySectionWrites: Writes[ApiLegacySection] = Json.writes[ApiLegacySection]
  implicit lazy val legacyPressedSectionWrites: Writes[ApiLegacyPressedSection] = Json.writes[ApiLegacyPressedSection]
  implicit lazy val legacyListsWrites: Writes[ApiLegacyLists] = Json.writes[ApiLegacyLists]
}

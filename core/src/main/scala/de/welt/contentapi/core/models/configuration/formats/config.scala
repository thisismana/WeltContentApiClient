package de.welt.contentapi.core.models.configuration.formats

import de.welt.contentapi.core.models.configuration.{ApiChannel, ApiChannelAdData, ApiChannelData, ApiChannelMetadata, ApiChannelMetadataNew, ApiChannelTheme, ChannelId}
import de.welt.contentapi.core.models.StageFormats
import de.welt.contentapi.core.models.section2.Stage
import play.api.libs.functional.syntax._
import play.api.libs.json._

object reads {

  object FullChannelReads {

    import SimpleFormats._
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    implicit lazy val channelReads: Reads[ApiChannel] = (
      (__ \ "id").read[ChannelId] and
        (__ \ "data").read[ApiChannelData] and
        (__ \ "stages").readNullable[Seq[Stage]] and
        (__ \ "parent").lazyRead(Reads.optionWithNull(channelReads)) and
        (__ \ "children").lazyRead(Reads.seq[ApiChannel](channelReads)) and
        (__ \ "hasChildren").read[Boolean] and
        (__ \ "lastModifiedDate").read[Long] and
        (__ \ "metadata").readNullable[ApiChannelMetadataNew]
      ) (ApiChannel)
  }

  object PartialChannelReads {

    import SimpleFormats._

    implicit lazy val noChildrenReads: Reads[ApiChannel] = new Reads[ApiChannel] {
      override def reads(json: JsValue): JsResult[ApiChannel] = json match {
        case JsObject(underlying) ⇒ (for {
          id ← underlying.get("id").map(_.as[ChannelId])
          data ← underlying.get("data").map(_.as[ApiChannelData])
        } yield JsSuccess(
          ApiChannel(
            id = id,
            data = data,
            stages = underlying.get("stages").flatMap(_.asOpt[Seq[Stage]]),
            metadata = underlying.get("metadata").flatMap(_.asOpt[ApiChannelMetadataNew]),
            children = Seq.empty
          )))
          .getOrElse(JsError("Could not validate json [something is missing]. " + Json.prettyPrint(json)))

        case err@_ ⇒ JsError(s"expected js-object, but was $err")
      }
    }
  }

}

object writes {

  object FullChannelWrites {

    import SimpleFormats._
    import StageFormats._
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    implicit lazy val channelWrites: Writes[ApiChannel] = (
      (__ \ "id").write[ChannelId] and
        (__ \ "data").write[ApiChannelData] and
        (__ \ "stages").writeNullable[Seq[Stage]] and
        (__ \ "parent").lazyWrite(Writes.optionWithNull(PartialChannelWrites.writeChannelAsNull)) and // avoid loops
        (__ \ "children").lazyWrite(Writes.seq[ApiChannel](channelWrites)) and
        (__ \ "hasChildren").write[Boolean] and
        (__ \ "lastModifiedDate").write[Long] and
        (__ \ "metadata").writeNullable[ApiChannelMetadataNew]
      ) (unlift(ApiChannel.unapply))

  }

  object PartialChannelWrites {

    import SimpleFormats._
    import StageFormats._

    implicit lazy val noChildrenWrites = new Writes[ApiChannel] {
      override def writes(o: ApiChannel): JsValue = JsObject(Map(
        "id" → Json.toJson(o.id),
        "lastModifiedDate" → JsNumber(o.lastModifiedDate),
        "hasChildren" → JsBoolean(o.hasChildren),
        "data" → Json.toJson(o.data)
      ) ++ o.stages.map { stage ⇒ "stages" → Json.toJson(stage) }
        ++ o.metadata.map { metadata ⇒ "metadata" → Json.toJson(metadata) }
      )
    }

    implicit lazy val oneLevelOfChildren: Writes[ApiChannel] = (
      (__ \ "id").write[ChannelId] and
        (__ \ "data").write[ApiChannelData] and
        (__ \ "stages").writeNullable[Seq[Stage]] and
        (__ \ "parent").lazyWrite(Writes.optionWithNull(noChildrenWrites)) and
        (__ \ "children").lazyWrite(Writes.seq[ApiChannel](noChildrenWrites)) and
        (__ \ "hasChildren").write[Boolean] and
        (__ \ "lastModifiedDate").write[Long] and
        (__ \ "metadata").writeNullable[ApiChannelMetadataNew]
      ) (unlift(ApiChannel.unapply))

    implicit lazy val writeChannelAsNull: Writes[ApiChannel] = new Writes[ApiChannel] {
      override def writes(o: ApiChannel): JsValue = JsNull
    }
  }

}

sealed trait Datasource {

  def typ: String

  def maxSize: Option[Int]
}

object DatasourceTypes {
  val curatedSource = "curated"
  val searchSource = "search"
}

object Datasource {

  import play.api.libs.json._

  def unapply(datasource: Datasource): Option[(String, JsValue)] = {
    val (typ: String, sub) = datasource.typ match {
      case DatasourceTypes.curatedSource =>
        val typedSource = datasource.asInstanceOf[CuratedSource]
        (typedSource.typ, Json.toJson(typedSource)(StageFormats.curatedSourceFormat))
      case DatasourceTypes.searchSource =>
        val typedSource = datasource.asInstanceOf[SearchSource]
        (typedSource.typ, Json.toJson(typedSource)(StageFormats.searchSourceFormat))
    }
    Some(typ -> sub)
  }

  def apply(typ: String, sourceParams: JsValue): Datasource = {
    (typ match {
      case DatasourceTypes.curatedSource => Json.fromJson[CuratedSource](sourceParams)(StageFormats.curatedSourceFormat)
      case DatasourceTypes.searchSource => Json.fromJson[SearchSource](sourceParams)(StageFormats.searchSourceFormat)
    }).get
  }

  case class CuratedSource(override val maxSize: Option[Int],
                           papyrusFolder: String,
                           papyrusFile: String
                          ) extends Datasource {
    override val typ: String = DatasourceTypes.curatedSource
  }

  case class SearchSource(override val maxSize: Option[Int],
                          queries: Seq[Query] = Seq()) extends Datasource {
    override val typ: String = DatasourceTypes.searchSource
  }

}


sealed trait Query {
  val filterNegative: Boolean
  val queryType: String

  override def toString = queryType
}

object Query {

  import play.api.libs.json._

  object QueryTypes {
    val typesQuery: String = "type"
    val subTypesQuery: String = "subType"
    val sectionsQuery: String = "section"
    val flagsQuery: String = "flags"
  }

  def unapply(query: Query): Option[(String, JsValue)] = {
    val (typ: String, sub) = query.queryType match {
      case QueryTypes.typesQuery =>
        val typedQuery = query.asInstanceOf[TypeQuery]
        (typedQuery.queryType, Json.toJson(typedQuery)(StageFormats.typeQueryFormat))
      case QueryTypes.subTypesQuery =>
        val typedQuery = query.asInstanceOf[SubTypeQuery]
        (typedQuery.queryType, Json.toJson(typedQuery)(StageFormats.subtypeQueryFormat))
      case QueryTypes.sectionsQuery =>
        val typedQuery = query.asInstanceOf[SectionQuery]
        (typedQuery.queryType, Json.toJson(typedQuery)(StageFormats.sectionQueryFormat))
      case QueryTypes.flagsQuery =>
        val typedQuery = query.asInstanceOf[FlagQuery]
        (typedQuery.queryType, Json.toJson(typedQuery)(StageFormats.flagQueryFormat))
    }
    Some(typ -> sub)
  }

  def apply(typ: String, query: JsValue): Query = {
    (typ match {
      case QueryTypes.typesQuery ⇒ Json.fromJson(query)(StageFormats.typeQueryFormat)
      case QueryTypes.subTypesQuery ⇒ Json.fromJson(query)(StageFormats.subtypeQueryFormat)
      case QueryTypes.sectionsQuery ⇒ Json.fromJson(query)(StageFormats.sectionQueryFormat)
      case QueryTypes.flagsQuery ⇒ Json.fromJson(query)(StageFormats.flagQueryFormat)
    }).get
  }

  case class TypeQuery(override val filterNegative: Boolean, queryValue: String)
    extends Query {
    override val queryType: String = QueryTypes.typesQuery
  }

  case class SubTypeQuery(override val filterNegative: Boolean, queryValue: String)
    extends Query {
    override val queryType: String = QueryTypes.subTypesQuery
  }

  case class SectionQuery(override val filterNegative: Boolean, queryValue: String)
    extends Query {
    override val queryType: String = QueryTypes.sectionsQuery
  }

  case class FlagQuery(override val filterNegative: Boolean, queryValues: Seq[String])
    extends Query {
    override val queryType: String = QueryTypes.flagsQuery
  }

}

object ChannelFormatNoChildren {
  implicit lazy val channelFormat: Format[ApiChannel] = Format(reads.PartialChannelReads.noChildrenReads, PartialChannelWrites.noChildrenWrites)
}

object SimpleFormats {

  implicit lazy val idFormat: Format[ChannelId] = Json.format[ChannelId]
  implicit lazy val dataFormat: Format[ApiChannelData] = Json.format[ApiChannelData]
  implicit lazy val metaDataFormat: Format[ApiChannelMetadata] = Json.format[ApiChannelMetadata]
  implicit lazy val channelThemeFormat: Format[ApiChannelTheme] = Json.format[ApiChannelTheme]
  implicit lazy val adFormat: Format[ApiChannelAdData] = Json.format[ApiChannelAdData]
  implicit lazy val metaDataNewFormat: Format[ApiChannelMetadataNew] = Json.format[ApiChannelMetadataNew]

}



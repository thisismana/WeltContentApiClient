package de.welt.contentapi.core.client.models

import de.welt.contentapi.utils.Loggable

case class ApiContentSearch(`type`: MainTypeParam = MainTypeParam(),
                            subType: SubTypeParam = SubTypeParam(),
                            section: SectionParam = SectionParam(),
                            homeSection: HomeSectionParam = HomeSectionParam(),
                            sectionExcludes: SectionExcludes = SectionExcludes(),
                            flags: FlagParam = FlagParam(),
                            limit: LimitParam = LimitParam(),
                            page: PageParam = PageParam()
                           ) {
  def allParams = Seq(`type`, subType, section, homeSection, sectionExcludes, flags, limit, page)

  def getAllParamsUnwrapped: Seq[(String, String)] = allParams.flatMap(_.asTuple)
}

sealed trait AbstractParam[T] {

  def name: String

  def value: T

  def valueToStringOpt: T ⇒ Option[String]

  def asTuple: Option[(String, String)] = valueToStringOpt(value).map { v ⇒ (name, v) }
}

abstract class ListParam[T](override val value: List[T]) extends AbstractParam[List[T]] {
  // conjunction by default
  def operator: String = ","

  override def valueToStringOpt: List[T] ⇒ Option[String] = {
    case Nil ⇒ None
    case nonEmpty ⇒ Some(nonEmpty.mkString(operator))
  }
}

abstract class ValueParam[T](override val value: T) extends AbstractParam[T] with Loggable {
  override def valueToStringOpt: T ⇒ Option[String] = {
    case s: String if s.nonEmpty ⇒ Some(s)
    case _: String ⇒ None
    case i: Int if i != Int.MinValue ⇒ Some(i.toString)
    case _: Int ⇒ None
    case unknown@_ ⇒
      log.info(s"Unknown value type: ${unknown.getClass.toString}")
      None
  }
}

case class MainTypeParam(override val value: List[String] = Nil) extends ListParam[String](value) {
  def this(singleValue: String) {
    this(List(singleValue))
  }
  override val name: String = "type"
}

case class SubTypeParam(override val value: List[String] = Nil) extends ListParam[String] (value){
  def this(singleValue: String) {
    this(List(singleValue))
  }
  override val name: String = "subType"
}

case class SectionParam(override val value: List[String] = Nil) extends ListParam[String](value) {
  def this(singleValue: String) {
    this(List(singleValue))
  }
  override val name: String = "sectionPath"

  override def operator: String = "|" // disjunction by for sectionPath
}

case class HomeSectionParam(override val value: List[String] = Nil) extends ListParam[String] (value){
  def this(singleValue: String) {
    this(List(singleValue))
  }
  override val name: String = "sectionHome"

  override def operator: String = "|" // disjunction by for sectionPath
}

case class SectionExcludes(override val value: List[String] = Nil) extends ListParam[String](value) {
  def this(singleValue: String) {
    this(List(singleValue))
  }
  override val name: String = "excludeSections"
}

case class FlagParam(override val value: List[String] = Nil) extends ListParam[String] (value){
  def this(singleValue: String) {
    this(List(singleValue))
  }
  override val name: String = "flag"
}

case class LimitParam(override val value: Int = Int.MinValue) extends ValueParam[Int](value) {
  override val name: String = "pageSize"
}

case class PageParam(override val value: Int = Int.MinValue) extends ValueParam[Int](value) {
  override val name: String = "page"
}

sealed trait Datasource {
  val `type`: String
  val maxSize: Option[Int]
}

case class CuratedSource(override val maxSize: Option[Int],
                         folderPath: String,
                         filename: String
                        ) extends Datasource {
  override val `type`: String = "curated"
}

case class SearchSource(override val maxSize: Option[Int],
                        queries: Seq[AbstractParam[_]] = Seq()) extends Datasource {
  override val `type`: String = "search"
}


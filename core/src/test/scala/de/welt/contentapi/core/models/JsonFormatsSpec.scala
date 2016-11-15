package de.welt.contentapi.core.models

import de.welt.contentapi.core.models.content._
import de.welt.contentapi.core.models.section.ApiSection
import de.welt.contentapi.core.models.section.stage._
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Json}


class JsonFormatsSpec extends FlatSpec with Matchers {

  "JSON Formats" should "work for SectionPage model and therefore underlying models" in {

    // from Json to Model

    val apiContentRaw = "{\"id\":\"159494746\",\"webUrl\":\"/testgpr/article159494746/T3N-Teaser-Testartikel.html\",\"apiUrl\":\"https://content-api.up.welt.de/content/159494746\",\"subType\":\"news\",\"fields\":{\"sourceId\":\"57b363f4-658f-4045-a512-58f92ea4a947\",\"commentState\":\"read-write\",\"seoMetaRobots\":\"index,follow,noodp\",\"articleTextSource\":\"<p>ArticleTest</p>\",\"seoTitle\":\"T3N-Teaser Testartikel\",\"headline\":\"JSON WORKS\",\"publicationDate\":\"2016-11-14T15:50:54Z\",\"readingTimeMinutes\":\"1\"},\"metadata\":{\"validFromDate\":\"0000-01-01T00:00:00Z\",\"validToDate\":\"2999-04-26T00:00:00Z\",\"state\":\"published\",\"fetchErrors\":0,\"transformationDate\":\"2016-11-14T15:51:25.974Z\",\"textEnrichmentDate\":\"2016-11-14T15:51:25.832Z\"},\"elements\":[{\"type\":\"oembed\",\"id\":\"2ff89abe-15cc-4509-95ac-b3e778bed430\",\"relations\":[\"inline\"],\"assets\":[{\"type\":\"metadata\",\"fields\":{\"caption\":\"Dieser Artikel stammt aus einer Kooperation mit dem Magazin t3n. Klicken Sie auf diese Links, verlassen Sie welt.de und landen auf den Artikeln bei t3n.de.\",\"escenicId\":\"159494836\",\"headline\":\"T3N Kooperation (oEmbed)\"}},{\"type\":\"url\",\"fields\":{\"url\":\"https://www.welt.de/oembed/?type=cooperation_links&partner=t3n\"}}]}],\"state\":\"published\",\"onward\":[],\"sections\":{\"home\":\"/testgpr/\",\"all\":[\"/testgpr/\"]},\"tags\":[],\"authors\":[],\"type\":\"article\"}"
    val apiContentJsValue: JsValue = Json.parse(apiContentRaw)

    import de.welt.contentapi.core.models.content.ApiContentReads._
    val result: JsResult[ApiContent] = apiContentJsValue.validate[ApiContent]

    val maybeContent: Option[ApiContent] = result match {
      case s: JsSuccess[String] => s.asOpt
      case e: JsError =>
        println("Errors: " + JsError.toJson(e).toString())
        Option.empty
    }

    val teaser: Teaser = Teaser(
      teaserConfig = TeaserConfig(
        teaserProfile = "Hero",
        teaserType = "Default"),
      data = maybeContent.get
    )

    val stage: Stage = Stage(
      stageLayoutId = "sectionHero",
      stageLabel = Some("Nice Section"),
      teasers = Seq(teaser)
    )

    val sectionPageFromJson: ApiSection = ApiSection(stages = Seq(stage))

    sectionPageFromJson.stages.size should be === 1

    // from Model back to Json

    import de.welt.contentapi.core.models.section.SectionPageFormats._
    val json: JsValue = Json.toJson(sectionPageFromJson)

    val result1: JsResult[ApiSection] = json.validate[ApiSection]

    val maybeSectionpage: Option[ApiSection]= result1 match {
      case s: JsSuccess[String] => s.asOpt
      case e: JsError =>
        println("Errors: " + JsError.toJson(e).toString())
        Option.empty
    }

     maybeSectionpage.get should be === sectionPageFromJson
  }

}

package de.welt.contentapi.core.models

import de.welt.contentapi.core.models.content._
import de.welt.contentapi.core.models.section.ApiSection
import de.welt.contentapi.core.models.section.stage._
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Json}


class JsonFormatsSpec extends FlatSpec with Matchers {

  "JSON Formats for ApiContent" should "validate from String" in {

    // from Json to Model

    val apiContentRaw =
      """
        |{
        |"id": "159504263",
        |"webUrl": "/newsticker/dpa_nt/infoline_nt/sport_nt/article159504263/Coach-Kramny-soll-Arminia-Bielefeld-retten.html",
        |"apiUrl": "https://content-api.up.welt.de/content/159504263",
        |"subType": "ticker",
        |"fields": {
        |"sourceId": "urn-newsml-dpa-com-20090101-161115-99-184534",
        |"keywords": "Fußball,2. Bundesliga,Bielefeld,Deutschland",
        |"seoMetaRobots": "index,follow,noodp",
        |"articleTextSource": "<p>Bielefeld - Jürgen Kramny soll Arminia Bielefeld vor dem Sturz in die Drittklassigkeit bewahren.</p><p>«Ich habe Arminia Bielefeld als Trainer und Spieler immer als einen spannenden Verein mit einer tollen Atmosphäre bei seinen Heimspielen wahrgenommen und freue mich auf diese neue Herausforderung», sagte der 45 Jahre alte Fußball-Lehrer, der mit dem VfB Stuttgart in die 2. Bundesliga abgestiegen war. «Hier hat es immer nach Fußball gerochen und geschmeckt.»</p><p>Mit den Verantwortlichen des jetzigen VfB-Ligakonkurrenten nahm Kramny erstmals in der vergangenen Woche Kontakt auf. «Für meine Entscheidungsfindung habe ich nicht lange gebraucht», sagte er. Seine kurzfristigen Ziele sind klar: «Bis Weihnachten wollen wir die für uns optimale Ausbeute holen und uns in bessere Bereiche spielen.»</p><p>Kramny ist Nachfolger des am 22. Oktober beurlaubten Rüdiger Rehm, der die Arminia erst zu Saisonbeginn übernommen hatte. Er bekommt einen Vertrag bis Saisonende. Sollte er mit der Mannschaft den Klassenverbleib schaffen, verlängert sich der Kontrakt um ein weiteres Jahr.</p><p>«Es wird für uns in den kommenden Wochen und Monaten sehr darauf ankommen, dass wir Geschlossenheit zeigen und an einem Strang ziehen. Dann können wir unser gemeinsames Ziel erreichen», sagte Kramny. Er wird den Tabellenvorletzten an diesem Freitag im Aufeinandertreffen mit dem drittplatzierten 1. FC Heidenheim erstmals von der Bank aus betreuen.</p><p>Arminias Sport-Geschäftsführer Samir Arabi bezeichnete Kramny als akribischen Arbeiter, der «voller Ehrgeiz und Tatendrang» sei. Arabi: «Wir sind überzeugt, dass er der richtige Cheftrainer für unseren weiteren Weg ist.» Alibis habe das Team nun nicht mehr, betonte Arabi: «Die Mannschaft steht in der Pflicht.»</p><p>Die Voraussetzungen für Kramny sind nicht einfach. Nach zwölf Spieltagen liegt die Arminia mit einem Sieg und acht Punkten als 17. zwei Zähler vor dem FC St. Pauli. Besser sieht es im DFB-Pokal aus: Nach Erfolgen bei Rot-Weiss Essen und Dynamo Dresden steht Bielefeld im Achtelfinale und hat im Februar beim Südwest-Regionalligisten FC-Astoria Walldorf eine vermeintlich leichte Aufgabe.</p><p><a href=\"http://www.arminia-bielefeld.de/2016/11/15/juergen-kramny-ist-neuer-cheftrainer-beim-dsc/\">Vereinsmitteilung</a></p>",
        |"lastModifiedDate": "2016-11-15T15:31:01Z",
        |"articleText": "<p>Bielefeld - Jürgen Kramny soll Arminia Bielefeld vor dem Sturz in die Drittklassigkeit bewahren.</p><p>«Ich habe Arminia Bielefeld als Trainer und Spieler immer als einen spannenden Verein mit einer tollen Atmosphäre bei seinen Heimspielen wahrgenommen und freue mich auf diese neue Herausforderung», sagte der 45 Jahre alte Fußball-Lehrer, der mit dem <a href=\"https://www.welt.de/sport/fussball/bundesliga/vfb-stuttgart/\" title=\"News und Hintergründe zum VfB Stuttgart finden Sie in unserem Themenspecial.\" name=\"\">VfB Stuttgart</a> in die 2. Bundesliga abgestiegen war. «Hier hat es immer nach Fußball gerochen und geschmeckt.»</p><p>Mit den Verantwortlichen des jetzigen VfB-Ligakonkurrenten nahm Kramny erstmals in der vergangenen Woche Kontakt auf. «Für meine Entscheidungsfindung habe ich nicht lange gebraucht», sagte er. Seine kurzfristigen Ziele sind klar: «Bis Weihnachten wollen wir die für uns optimale Ausbeute holen und uns in bessere Bereiche spielen.»</p><p>Kramny ist Nachfolger des am 22. Oktober beurlaubten Rüdiger Rehm, der die Arminia erst zu Saisonbeginn übernommen hatte. Er bekommt einen Vertrag bis Saisonende. Sollte er mit der Mannschaft den Klassenverbleib schaffen, verlängert sich der Kontrakt um ein weiteres Jahr.</p><p>«Es wird für uns in den kommenden Wochen und Monaten sehr darauf ankommen, dass wir Geschlossenheit zeigen und an einem Strang ziehen. Dann können wir unser gemeinsames Ziel erreichen», sagte Kramny. Er wird den Tabellenvorletzten an diesem Freitag im Aufeinandertreffen mit dem drittplatzierten 1. FC Heidenheim erstmals von der Bank aus betreuen.</p><p>Arminias Sport-Geschäftsführer Samir Arabi bezeichnete Kramny als akribischen Arbeiter, der «voller Ehrgeiz und Tatendrang» sei. Arabi: «Wir sind überzeugt, dass er der richtige Cheftrainer für unseren weiteren Weg ist.» Alibis habe das Team nun nicht mehr, betonte Arabi: «Die Mannschaft steht in der Pflicht.»</p><p>Die Voraussetzungen für Kramny sind nicht einfach. Nach zwölf Spieltagen liegt die Arminia mit einem Sieg und acht Punkten als 17. zwei Zähler vor dem <a href=\"http://www.welt.de/themen/fc-st-pauli/\" title=\"News und Hintergründe zum FC St. Pauli finden Sie auf unserer Themenseite.\" name=\"\">FC St. Pauli</a>. Besser sieht es im <a href=\"http://www.welt.de/themen/dfb-pokal/\" title=\"Ergebnisse, News, Termine und Hintergründe zum DFB-Pokal finden Sie auf unserer Themenseite\" name=\"\">DFB-Pokal</a> aus: Nach Erfolgen bei <a href=\"http://www.welt.de/themen/rot-weiss-essen/\" title=\"News und Hintergründe zu Rot-Weiss Essen finden Sie auf unserer Themenseite.\" name=\"\">Rot-Weiss Essen</a> und <a href=\"http://www.welt.de/themen/sg-dynamo-dresden/\" title=\"News und Hintergründe zu Dynamo Dresden finden Sie auf unserer Themenseite.\" name=\"\">Dynamo Dresden</a> steht Bielefeld im Achtelfinale und hat im Februar beim Südwest-Regionalligisten FC-Astoria Walldorf eine vermeintlich leichte Aufgabe.</p><p><a href=\"http://www.arminia-bielefeld.de/2016/11/15/juergen-kramny-ist-neuer-cheftrainer-beim-dsc/\">Vereinsmitteilung</a></p>",
        |"boerse": "dpakeyword::Fußball||2. Bundesliga||Bielefeld||Deutschland",
        |"source": "dpa-infocom GmbH",
        |"seoTitle": "Coach Kramny soll Arminia Bielefeld retten",
        |"readingTimeMinutes": "2",
        |"commentState": "read-write",
        |"vgWort": "https://ssl-welt.met.vgwort.de/na/0f1c4ec43fc54432b99ac653b43a5fe4",
        |"headline": "Coach Kramny soll Arminia Bielefeld retten",
        |"publicationDate": "2016-11-15T15:26:17Z"
        |},
        |"metadata": {
        |"validFromDate": "0000-01-01T00:00:00Z",
        |"validToDate": "2999-04-26T00:00:00Z",
        |"state": "published",
        |"fetchErrors": 0,
        |"transformationDate": "2016-11-15T15:31:03.722Z",
        |"textEnrichmentDate": "2016-11-15T15:31:03.686Z"
        |},
        |"elements": [
        |{
        |"type": "themepage",
        |"id": "",
        |"relations": [
        |"themepage"
        |],
        |"assets": [
        |{
        |"type": "metadata",
        |"fields": {
        |"escenicId": "142762771",
        |"label": "Bielefeld",
        |"url": "https://www.welt.de/themen/bielefeld/"
        |}
        |}
        |]
        |},
        |{
        |"type": "image",
        |"id": "e0fa3b6e-c17b-4318-9fc1-fc2cc1f03a71",
        |"relations": [
        |"opener"
        |],
        |"assets": [
        |{
        |"type": "metadata",
        |"fields": {
        |"copyright": "dpa",
        |"altText": "Jürgen Kramny",
        |"escenicId": "159504261"
        |},
        |"metadata": {
        |"validFromDate": "0000-01-01T00:00:00Z",
        |"validToDate": "2999-04-26T00:00:00Z",
        |"state": "published",
        |"fetchErrors": 0,
        |"transformationDate": "2016-11-15T15:31:03.722Z"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "400",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8322641097-ci2x3l-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "600"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "300",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8320311097-ci3x2s-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "200"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "230",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8321621097-ci23x11-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "110"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "940",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8326581097-ci94x35-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "350"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "1500",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8322501097-ci102l-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "1467"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "320",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8321351097-ci16x9-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "180"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "-1",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8327931097-coriginal-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "1"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "600",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8320241097-ci3x2l-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "400"
        |}
        |}
        |]
        |},
        |{
        |"type": "image",
        |"id": "97112412-8de4-4def-893f-ce6b06546b8a",
        |"relations": [
        |"teaser"
        |],
        |"assets": [
        |{
        |"type": "metadata",
        |"fields": {
        |"copyright": "dpa",
        |"altText": "Jürgen Kramny",
        |"escenicId": "159504261"
        |},
        |"metadata": {
        |"validFromDate": "0000-01-01T00:00:00Z",
        |"validToDate": "2999-04-26T00:00:00Z",
        |"state": "published",
        |"fetchErrors": 0,
        |"transformationDate": "2016-11-15T15:31:03.722Z"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "400",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8322641097-ci2x3l-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "600"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "300",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8320311097-ci3x2s-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "200"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "230",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8321621097-ci23x11-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "110"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "940",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8326581097-ci94x35-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "350"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "1500",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8322501097-ci102l-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "1467"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "320",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8321351097-ci16x9-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "180"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "-1",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8327931097-coriginal-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "1"
        |}
        |},
        |{
        |"type": "image",
        |"fields": {
        |"width": "600",
        |"source": "https://www.welt.de/img/newsticker/dpa_nt/infoline_nt/sport_nt/mobile159504261/8320241097-ci3x2l-wWIDTH/urn-newsml-dpa-com-20090101-161115-99-184566-large-4-3-jpg.jpg",
        |"height": "400"
        |}
        |}
        |]
        |}
        |],
        |"state": "published",
        |"onward": [],
        |"sections": {
        |"home": "/newsticker/dpa_nt/infoline_nt/sport_nt/",
        |"all": [
        |"/newsticker/dpa_nt/infoline_nt/sport_nt/",
        |"/newsticker/dpa_nt/infoline_nt/",
        |"/newsticker/dpa_nt/",
        |"/newsticker/"
        |]
        |},
        |"tags": [
        |{
        |"id": "Fußball",
        |"value": "tag:won@welt.de,2011:thing-5003783"
        |},
        |{
        |"id": "Deutschland",
        |"value": "tag:won@welt.de,2011:location-6000198"
        |},
        |{
        |"id": "Zweite Liga",
        |"value": "tag:won@welt.de,2011:thing-5003816"
        |},
        |{
        |"id": "Bielefeld",
        |"value": "tag:won@welt.de,2011:Bielefeld"
        |}
        |],
        |"authors": [],
        |"type": "article"
        |}
      """.stripMargin
    val apiContentJsValue: JsValue = Json.parse(apiContentRaw)

    import de.welt.contentapi.core.models.content.ApiContentReads._
    val result: JsResult[ApiContent] = apiContentJsValue.validate[ApiContent]

    val maybeContent: Option[ApiContent] = result match {
      case s: JsSuccess[String] => s.asOpt
      case e: JsError => Option.empty
    }
  }
  "JSON Formats for ApiSection" should "be equal when writing to Json and validating from Json to ApiSection" in {
    val expectedApiSection: ApiSection = getSampleApiSection

    import de.welt.contentapi.core.models.section.ApiSectionFormat._
    val apiSectionAsJsValue: JsValue = Json.toJson(expectedApiSection)
    val jsResultForJsValue: JsResult[ApiSection] = apiSectionAsJsValue.validate[ApiSection]

    val maybeApiSectionFromJson: Option[ApiSection]= jsResultForJsValue match {
      case s: JsSuccess[String] => s.asOpt
      case e: JsError => Option.empty
    }

     maybeApiSectionFromJson.get should be === expectedApiSection
  }

  def getSampleApiSection: ApiSection = {
    val apiContent: ApiContent = ApiContent(webUrl = "/foo", `type` = "news")
    val teaser: ApiTeaser = ApiTeaser(
      teaserConfig = ApiTeaserConfig(
        teaserProfile = "Hero",
        teaserType = "Default"),
      data = apiContent
    )
    val stage: ApiStage = ApiStage(
      stageLayoutId = "sectionHero",
      stageLabel = Some("Nice Section"),
      teasers = Seq(teaser)
    )
    ApiSection(stages = Seq(stage))
  }

}

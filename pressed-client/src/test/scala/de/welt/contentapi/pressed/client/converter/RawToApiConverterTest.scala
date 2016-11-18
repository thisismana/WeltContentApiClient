package de.welt.contentapi.pressed.client.converter

import de.welt.contentapi.raw.models._
import org.scalatest.words.MustVerb
import org.scalatest.{FlatSpec, Matchers}

class RawToApiConverterTest extends FlatSpec with Matchers with TestScope with MustVerb {

  val converter: RawToApiConverter = new RawToApiConverter()

  "Converter" should "calculate 'sport' as adTag" in {
      converter.calculateAdTech(homeSportFussball) shouldBe homeSport.id.path.replaceAll("/", "")
  }

  it should "calculate 'sport' as videoAdTech" in {
    converter.calculateVideoAdTech(homeSportFussball) shouldBe homeSport.id.path.replaceAll("/", "")
  }

}


trait TestScope {
  val rawChannelMetaTags: RawChannelMetaTags = RawChannelMetaTags(
    title = Some("Meta Title"),
    description = Some("Meta Description"),
    keywords = Some(Seq("Keyword1", "Keyword2")),
    contentRobots = Some(RawChannelMetaRobotsTag(noFollow = Some(false), noIndex = Some(false))),
    sectionRobots = Some(RawChannelMetaRobotsTag(noFollow = Some(true), noIndex = Some(true)))
  )

  val fussballRawChannelHeader: RawChannelHeader = RawChannelHeader(
    sponsoring = Some("tagHeuer"),
    logo = Some("fussball_logo"),
    slogan = Some("Make Fosbal great again"),
    label = Some("Fussball"),
    sectionReferences = Some(Seq(RawSectionReference(label = Some("Alle Fussball News"), path = Some("/sport/fussball"))))
  )

  val falseRawChannelCommercial: RawChannelCommercial = RawChannelCommercial(
    definesAdTag = Some(false),
    definesVideoAdTag = Some(false)
  )
  val trueRawChannelCommercial: RawChannelCommercial = RawChannelCommercial(
    definesAdTag = Some(true),
    definesVideoAdTag = Some(true)
  )

  val homeSportFussball: RawChannel = RawChannel(
    RawChannelId(
      path = "/sport/fussball/",
      label = "Fussball"
    ),
    parent = Some(homeSport),
    children = Some(Seq.empty),
    config = Some(
      RawChannelConfiguration(
        commercial = Some(falseRawChannelCommercial),
        metaTags = Some(rawChannelMetaTags),
        header = Some(fussballRawChannelHeader)
      )
    )
  )
  val homeSport: RawChannel = RawChannel(
    RawChannelId(
      path = "/sport/",
      label = "Sport"
    ),
    parent = Some(home),
    children = Some(Seq(homeSportFussball)),
    config = Some(RawChannelConfiguration(commercial = Some(trueRawChannelCommercial)))
  )
  val home: RawChannel = RawChannel(
    RawChannelId(
      path = "/",
      label = "home"
    ),
    parent = None,
    children = Some(Seq(homeSport)),
    config = Some(RawChannelConfiguration(commercial = Some(trueRawChannelCommercial)))
  )
}

package de.welt.contentapi.pressed.client.converter

import de.welt.contentapi.pressed.models.ApiThemeConfiguration
import de.welt.contentapi.raw.models._
import org.scalatest.words.MustVerb
import org.scalatest.{FlatSpec, Matchers}

class RawToApiConverterTest extends FlatSpec with Matchers with TestScope with MustVerb {

  val converter: RawToApiConverter = new RawToApiConverter()

  "AdTag Converter" should "calculate 'sport' as AdTag as the first in line with 'definesAdTag == true'" in {
    converter.calculateAdTag(homeSportFussball) shouldBe homeSport.id.path.replaceAll("/", "")
  }
  it should "calculate 'sport' as VideoAdTag as the first in line with 'definesVideoAdTag == true'" in {
    converter.calculateVideoAdTag(homeSportFussball) shouldBe homeSport.id.path.replaceAll("/", "")
  }
  it should "calculate 'sonstiges' AdTag because no `rootLevel` section has 'definesAdTag == true'" in {
    converter.calculateAdTag(homePolitikAusland) shouldBe "sonstiges"
  }
  it should "calculate 'sonstiges' as VideoAdTag because no `rootLevel` section has 'definesVideoAdTag == true'" in {
    converter.calculateVideoAdTag(homePolitikAusland) shouldBe "sonstiges"
  }

  "Theme Converter" should "always calculate `default` as Theme, because there is no implementation yet" in {
    converter.apiThemeFromRawChannel(homeSportFussball) shouldBe ApiThemeConfiguration(name = Some("default"))
  }





}


trait TestScope {
  val rawChannelMetaTags = RawChannelMetadata(
    title = Some("Meta Title"),
    description = Some("Meta Description"),
    keywords = Some(Seq("Keyword1", "Keyword2")),
    contentRobots = Some(RawChannelMetaRobotsTag(noFollow = Some(false), noIndex = Some(false))),
    sectionRobots = Some(RawChannelMetaRobotsTag(noFollow = Some(true), noIndex = Some(true)))
  )

  val fussballRawChannelHeader = RawChannelHeader(
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

  val home: RawChannel = RawChannel(
    RawChannelId(
      path = "/",
      label = "home"
    ),
    parent = None,
    children = None,
    config = Some(RawChannelConfiguration(commercial = Some(trueRawChannelCommercial)))
  )
  val homeSport: RawChannel = RawChannel(
    RawChannelId(
      path = "/sport/",
      label = "Sport"
    ),
    parent = Some(home),
    children = None,
    config = Some(RawChannelConfiguration(commercial = Some(trueRawChannelCommercial)))
  )
  val homeSportFussball: RawChannel = RawChannel(
    RawChannelId(
      path = "/sport/fussball/",
      label = "Fussball"
    ),
    parent = Some(homeSport),
    children = None,
    config = Some(
      RawChannelConfiguration(
        commercial = Some(falseRawChannelCommercial),
        metadata = Some(rawChannelMetaTags),
        header = Some(fussballRawChannelHeader)
      )
    )
  )


  val homePolitik: RawChannel = RawChannel(
    RawChannelId(
      path = "/politik/",
      label = "Politik"
    ),
    parent = Some(home),
    children = None,
    config = Some(RawChannelConfiguration(commercial = Some(falseRawChannelCommercial)))
  )
  val homePolitikAusland: RawChannel = RawChannel(
    RawChannelId(
      path = "/sport/ausland/",
      label = "Ausland"
    ),
    parent = Some(homePolitik),
    children = None,
    config = Some(
      RawChannelConfiguration(
        commercial = Some(falseRawChannelCommercial),
        metadata = Some(rawChannelMetaTags),
        header = Some(fussballRawChannelHeader)
      )
    )
  )
  
  
  
}

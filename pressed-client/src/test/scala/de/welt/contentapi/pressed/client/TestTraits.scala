package de.welt.contentapi.pressed.client

import de.welt.contentapi.raw.models._

object TestTraits {
  trait TestScopeWithParents {
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
      definesAdTag = false,
      definesVideoAdTag = false
    )
    val trueRawChannelCommercial: RawChannelCommercial = RawChannelCommercial(
      definesAdTag = true,
      definesVideoAdTag = true
    )

    val homeSportFussball: RawChannel = RawChannel(
      RawChannelId(
        path = "/sport/fussball/",
        label = "Fussball"
      ),
      parent = None,
      children = Seq.empty,
      config = RawChannelConfiguration(
        commercial = falseRawChannelCommercial,
        metadata = Some(rawChannelMetaTags),
        header = Some(fussballRawChannelHeader)
      )
    )
    val homeSport: RawChannel = RawChannel(
    RawChannelId(
    path = "/sport/",
    label = "Sport"
    ),
    parent = None,
    children = Seq(homeSportFussball),
    config = RawChannelConfiguration(commercial = trueRawChannelCommercial)
    )

    val home: RawChannel = RawChannel(
    RawChannelId(
    path = "/",
    label = "home"
    ),
    parent = None,
    children = Seq(homeSport),
    config = RawChannelConfiguration(commercial = trueRawChannelCommercial)
    )

    homeSport.updateParentRelations(Some(home))
    homeSportFussball.updateParentRelations(Some(homeSport))

    val homePolitik: RawChannel = RawChannel(
      RawChannelId(
        path = "/politik/",
        label = "Politik"
      ),
      parent = Some(home),
      children = Seq.empty,
      config = RawChannelConfiguration(commercial = falseRawChannelCommercial)
    )
    val homePolitikAusland: RawChannel = RawChannel(
      RawChannelId(
        path = "/sport/ausland/",
        label = "Ausland"
      ),
      parent = Some(homePolitik),
      children = Seq.empty,
      config =
        RawChannelConfiguration(
          commercial = falseRawChannelCommercial,
          metadata = Some(rawChannelMetaTags),
          header = Some(fussballRawChannelHeader)
        )
    )

  }

  trait TestScopeSimple {
    val rawChannelThemeTest: RawChannelTheme = RawChannelTheme(
      name = Some("theme"),
      fields = Some(Map("key1" -> "value2",
        "key2" -> "value2")))

    val rawChannelConfigurationTest = RawChannelConfiguration(
      metadata = Some(
        RawChannelMetadata(title = Some("metadata"),
          contentRobots = Some(RawChannelMetaRobotsTag(noFollow = Some(false), noIndex = Some(false))),
          sectionRobots = Some(RawChannelMetaRobotsTag(noFollow = Some(true), noIndex = Some(true)))
        )
      ),
      header = Some(
        RawChannelHeader(
          label = Some("label"),
          sponsoring = Some("sponsoring"),
          sectionReferences = Some(Seq(RawSectionReference(Some("Label"), Some("/Path/"))))
        )
      ),
      theme = Some(
        rawChannelThemeTest
      ),
      commercial = RawChannelCommercial(definesAdTag = true)
    )

    val rawChannelTest = RawChannel(
      id = RawChannelId(
        path = "/advertorial/",
        label = "Advertorial"
      ),
      config = rawChannelConfigurationTest
    )
  }

}

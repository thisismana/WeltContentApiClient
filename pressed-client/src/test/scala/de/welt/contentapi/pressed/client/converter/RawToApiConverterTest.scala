package de.welt.contentapi.pressed.client.converter

import de.welt.contentapi.core.models.ApiReference
import de.welt.contentapi.pressed.client.TestTraits.{TestScopeSimple, TestScopeWithParents}
import de.welt.contentapi.pressed.models.{ApiChannel, ApiConfiguration, ApiMetaConfiguration, ApiMetaRobots}
import org.scalatest.words.MustVerb
import org.scalatest.{FlatSpec, Matchers}

class RawToApiConverterTest extends FlatSpec with Matchers with MustVerb with TestScopeWithParents with TestScopeSimple {

  val converter: RawToApiConverter = new RawToApiConverter()

    "AdTag Calculator" must "calculate 'sport' as AdTag as the first in line with 'definesAdTag == true'" in {
      converter.calculatePathForAdTag(homeSportFussball) shouldBe "sport"
    }
    it must "calculate 'sport' as VideoAdTag as the first in line with 'definesVideoAdTag == true'" in {
      converter.calculatePathForVideoAdTag(homeSportFussball) shouldBe "sport"
    }
    it must "calculate 'sonstiges' AdTag because no `rootLevel` section has 'definesAdTag == true'" in {
      converter.calculatePathForAdTag(homePolitikAusland) shouldBe "sonstiges"
    }
    it must "calculate 'sonstiges' as VideoAdTag because no `rootLevel` section has 'definesVideoAdTag == true'" in {
      converter.calculatePathForVideoAdTag(homePolitikAusland) shouldBe "sonstiges"
    }

  "ApiChannel Converter" must "convert the breadcrumb, sorted from root to leafs with all 3 levels" in {
    val result: ApiChannel = converter.getApiChannelFromRawChannel(rawChannel = homeSportFussball)
    val breadcrumb: Seq[ApiReference] = result.breadcrumb.getOrElse(Seq.empty)
    breadcrumb.size shouldBe 3
    breadcrumb.head.href.getOrElse("") shouldBe "/"
    breadcrumb.last.href.getOrElse("") shouldBe "/sport/fussball/"
  }

  "ApiSectionReference Converter" must "have label and href from the RawChannel" in {
    val apiReferenceFussball: ApiReference = converter.getApiSectionReferenceFromRawChannel(rawChannel = homeSportFussball)
    apiReferenceFussball.href shouldBe Some("/sport/fussball/")
    apiReferenceFussball.label shouldBe Some("Fussball")
  }

  "ApiMetaConfiguration Converter" must "have title, tags(from keywords) and description from the RawChannel" in {
    val maybeApiMetaConfiguration: Option[ApiMetaConfiguration] = converter.apiMetaConfigurationFromRawChannel(rawChannel = homeSportFussball)
    val apiMetaConfiguration = maybeApiMetaConfiguration.getOrElse(throw new RuntimeException("Test failed. Should have been Some."))
    apiMetaConfiguration.title shouldBe rawChannelMetaTags.title
    apiMetaConfiguration.tags shouldBe rawChannelMetaTags.keywords
    apiMetaConfiguration.description shouldBe rawChannelMetaTags.description
  }

  it must "convert 'content' and 'section' meta robot tags from the RawChannel" in {
    val apiMetaConfiguration: ApiMetaConfiguration = converter
      .apiMetaConfigurationFromRawChannel(rawChannel = rawChannelTest)
      .getOrElse(throw new RuntimeException("Test failed. Should have been Some."))
    apiMetaConfiguration.contentMetaRobots.flatMap(_.noFollow) shouldBe rawChannelMetaTags.contentRobots.flatMap(_.noFollow)
    apiMetaConfiguration.contentMetaRobots.map(_.noIndex) shouldBe rawChannelMetaTags.contentRobots.map(_.noIndex)

    apiMetaConfiguration.sectionMetaRobots.map(_.noFollow) shouldBe rawChannelMetaTags.sectionRobots.map(_.noFollow)
    apiMetaConfiguration.sectionMetaRobots.map(_.noIndex) shouldBe rawChannelMetaTags.sectionRobots.map(_.noIndex)
  }

  "ApiMetaRobots Converter" must "convert content tags" in {
    val apiMetaRobots: ApiMetaRobots = converter.apiMetaRobotsFromRawChannelMetaRobotsTag(rawChannelMetaTags.contentRobots.get)
  }

  "ApiConfiguration Converter" must "convert commercial, theme, header, metadata and sponsoring with sub routines" in {
    // this is a high level test - all expected values are tested in tests above
    val apiConfiguration: ApiConfiguration = converter.apiConfigurationFromRawChannel(rawChannelTest)
    apiConfiguration.commercial.flatMap(_.pathForAdTag) shouldBe Some(rawChannelTest.id.path.replaceAll("/", ""))
    apiConfiguration.theme.flatMap(_.name) shouldBe rawChannelThemeTest.name
    apiConfiguration.header.flatMap(_.title) shouldBe rawChannelConfigurationTest.header.flatMap(_.label)
    apiConfiguration.meta.flatMap(_.title) shouldBe rawChannelConfigurationTest.metadata.flatMap(_.title)
    apiConfiguration.sponsoring.flatMap(_.name) shouldBe rawChannelConfigurationTest.header.flatMap(_.sponsoring)
  }

  "ApiTheme Converter" must "convert label and fields from the RawChannelTheme" in {
    val apiThemeConfiguration = converter.apiThemeFromRawChannel(rawChannel = rawChannelTest).getOrElse(throw new RuntimeException("Test failed!"))
    apiThemeConfiguration.name shouldBe rawChannelThemeTest.name
    apiThemeConfiguration.unwrappedFields shouldBe rawChannelThemeTest.unwrappedFields
  }

}




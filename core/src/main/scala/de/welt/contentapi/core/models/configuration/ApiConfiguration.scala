package de.welt.contentapi.core.models.configuration

/**
  * Configuration for a content or section page. All configs are optional.
  * This means that they can be overwritten (by ConfigMcConfigFace) but not required. All clients must define some kind
  * of fallback or default values.
  *
  * @param meta configuration for <meta> tag overrides
  * @param commercial commercial configuration
  * @param branding branding is part of the page header (page sponsoring). E.g. Formel1
  * @param header (content) page header configuration. Not the real page header.
  * @param theme theme of the page
  */
case class ApiConfiguration(meta: Option[ApiMetaConfiguration] = None,
                            commercial: Option[ApiCommercialConfiguration] = None,
                            branding: Option[ApiBrandingConfiguration] = None,
                            header: Option[ApiHeaderConfiguration] = None,
                            theme: Option[ApiThemeConfiguration] = None)

/**
  * <meta> configuration for content or section pages
  *
  * @param title <title> override
  * @param description <meta> description override
  * @param tags <meta> keyword override
  */
case class ApiMetaConfiguration(title: Option[String] = None,
                                description: Option[String] = None,
                                tags: Option[List[String]] = None) {
  lazy val unwrappedTags: List[String] = tags.getOrElse(Nil)
}

/**
  * Some overrides for commercial settings (ASMI). Per default all commercial configuration based on the section path.
  *
  * @param adTag ad tag for all banner ads
  * @param videoAdTag ad tag for all video ads
  */
case class ApiCommercialConfiguration(adTag: Option[String] = None, videoAdTag: Option[String] = None)

/**
  * Branding or sponsoring of section and content pages. This is only the name of the branding. The impl is part of
  * the client.
  *
  * @param name name of the branding. Need for mapping.
  */
case class ApiBrandingConfiguration(name: Option[String] = None)

/**
  * Some configuration for the section or content page header. Not the real page header.
  *
  * @param title title of page
  * @param sectionReferences section refs for linking
  */
case class ApiHeaderConfiguration(title: Option[String] = None,
                                  sectionReferences: Option[List[ApiSectionReference]] = None) {
  lazy val unwrappedSectionReferences: List[ApiSectionReference] = sectionReferences.getOrElse(Nil)
}

/**
  * Theme of the section or content page. Mostly for some background color changes e.g. mediathek. This is only
  * the name of the theme. The impl is part of the client.
  *
  * @param name name of the theme. Need for mapping.
  */
case class ApiThemeConfiguration(name: Option[String] = None)

/**
  * A reference <a/> to a section.
  *
  * Naming-Refactoring: Maybe is ApiReference better? Think of internal and external links. All internal links are
  * relative and all external absolute. And what about links to content pages?
  *
  * @param label label of the <a/>
  * @param path href of the <a/>
  */
case class ApiSectionReference(label: Option[String] = None, path: Option[String] = None)

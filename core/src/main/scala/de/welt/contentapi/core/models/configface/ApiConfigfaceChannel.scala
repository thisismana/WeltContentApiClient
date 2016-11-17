package de.welt.contentapi.core.models.configface

import de.welt.contentapi.core.models.pressed._

case class ApiConfigfaceChannel(meta: Option[ApiMetaConfiguration] = None,
                                commercial: Option[ApiCommercialConfiguration] = None,
                                branding: Option[ApiBrandingConfiguration] = None,
                                header: Option[ApiHeaderConfiguration] = None,
                                theme: Option[ApiThemeConfiguration] = None,
                                content: Option[ApiContentConfiguration] = None) {

}

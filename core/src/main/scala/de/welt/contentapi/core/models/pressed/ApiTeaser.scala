package de.welt.contentapi.core.models.pressed

import de.welt.contentapi.core.models.content.ApiContent


case class ApiTeaser(teaserConfig: ApiTeaserConfig,
                     data: ApiContent)

case class ApiTeaserConfig(profile: String,
                           `type`: String)

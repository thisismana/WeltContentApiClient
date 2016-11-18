package de.welt.contentapi.pressed.models

import de.welt.contentapi.core.models.ApiContent


case class ApiTeaser(teaserConfig: ApiTeaserConfig, data: ApiContent)

case class ApiTeaserConfig(profile: String, `type`: String)

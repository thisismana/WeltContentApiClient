package de.welt.contentapi.core.models.section.stage

import de.welt.contentapi.core.models.content.ApiContent


case class ApiTeaser(teaserConfig: ApiTeaserConfig,
                     data: ApiContent)

case class ApiTeaserConfig(teaserProfile: String,
                           teaserType: String)
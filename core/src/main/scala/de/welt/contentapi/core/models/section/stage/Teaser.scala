package de.welt.contentapi.core.models.section.stage

import de.welt.contentapi.core.models.content.ApiContent


case class Teaser(teaserConfig: TeaserConfig,
                  data: ApiContent)

case class TeaserConfig(teaserProfile: String,
                        teaserType: String)
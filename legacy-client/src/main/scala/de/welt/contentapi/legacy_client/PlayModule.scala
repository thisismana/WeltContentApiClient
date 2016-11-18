package de.welt.contentapi.legacy_client

import de.welt.contentapi.legacy_client.services.{AdminSectionService, AdminSectionServiceImpl, LegacySectionService, LegacySectionServiceImpl}
import play.api.inject.Module
import play.api.{Configuration, Environment}

class PlayModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration) = {
    val client = new de.welt.contentapi.core_client.PlayModule()
    client.bindings(environment, configuration) ++ Seq(
      // admin services
      bind(classOf[AdminSectionService]).to(classOf[AdminSectionServiceImpl]),
      bind(classOf[LegacySectionService]).to(classOf[LegacySectionServiceImpl])
    )
  }
}

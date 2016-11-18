package de.welt.contentapi.legacy

import play.api.inject.Module
import play.api.{Configuration, Environment}

class PlayModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration) = {

    val client = new de.welt.contentapi.core_client.PlayModule()

    client.bindings(environment, configuration) ++ Seq(
      // admin services
      //      bind(classOf[AdminSectionService]).to(classOf[AdminSectionServiceImpl]),
//      bind(classOf[LegacySectionService]).to(classOf[LegacySectionServiceImpl])

    )
  }
}

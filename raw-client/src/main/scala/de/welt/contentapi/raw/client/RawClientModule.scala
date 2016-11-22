package de.welt.contentapi.raw.client

import de.welt.contentapi.raw.client.services.{AdminSectionService, AdminSectionServiceImpl, SdpSectionDataService, SdpSectionDataServiceImpl}
import play.api.inject.Module
import play.api.{Configuration, Environment}

class RawClientModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration) = {
    val client = new de.welt.contentapi.core.client.PlayModule()
    client.bindings(environment, configuration) ++ Seq(
      // admin services
      bind(classOf[AdminSectionService]).to(classOf[AdminSectionServiceImpl]),
      bind(classOf[SdpSectionDataService]).to(classOf[SdpSectionDataServiceImpl])
    )
  }
}

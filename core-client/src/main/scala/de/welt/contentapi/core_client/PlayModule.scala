package de.welt.contentapi.core_client

import de.welt.contentapi.core_client.services.configuration.{ContentClientConfig, ContentClientConfigImpl}
import de.welt.contentapi.core_client.services.contentapi._
import de.welt.contentapi.core_client.services.s3.{S3Client, S3ClientImpl}
import play.api.inject.Module
import play.api.{Configuration, Environment}

class PlayModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration) = {
    Seq(

      bind(classOf[ContentClientConfig]).to(classOf[ContentClientConfigImpl]).eagerly,
      bind(classOf[ContentService]).to(classOf[ContentServiceImpl]).eagerly,

      bind(classOf[S3Client]).to(classOf[S3ClientImpl]),

      bind(classOf[SectionService]).to(classOf[SectionServiceImpl]),
      bind(classOf[ContentSearchService]).to(classOf[ContentSearchServiceImpl])

    )
  }
}

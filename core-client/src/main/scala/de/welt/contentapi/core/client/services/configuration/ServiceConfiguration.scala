package de.welt.contentapi.core.client.services.configuration

import play.api.Configuration

case class ServiceConfiguration(serviceName: String, host: String, endpoint: String, username: String, password: String)

object ServiceConfiguration {
  def apply(serviceName: String, config: Configuration): ServiceConfiguration =
    (for {
      host ← config.getString("host")
      endpoint ← config.getString("endpoint")
      username ← config.getString("credentials.username")
      password ← config.getString("credentials.password")
    } yield ServiceConfiguration(serviceName, host, endpoint, username, password)
    ) getOrElse { throw config.reportError(serviceName, s"Service at $serviceName was not configured correctly.") }
}
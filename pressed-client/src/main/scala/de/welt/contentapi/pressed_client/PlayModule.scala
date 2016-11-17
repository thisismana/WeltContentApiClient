package de.welt.contentapi.pressed_client

import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}

class PlayModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq()
}

package de.welt.contentapi.core.models.env

object env {
  sealed trait Env

  case object Preview extends Env

  case object Live extends Env

  case object UndefinedEnv extends Env

  object Env {
    def apply(env: String): Env = env match {
      case "preview" ⇒ Preview
      case "live" ⇒ Live
      case _ ⇒ throw new IllegalArgumentException(s"Not a valid env: $env. Allowed values are 'preview' and 'live'")
    }
  }

}

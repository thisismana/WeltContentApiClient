package de.welt.contentapi.core.models.env

object Env {
  sealed trait Env

  case object Preview extends Env
  case object Live extends Env
  case object UndefinedEnv extends Env

  def apply(env: String): Env = env match {
    case "preview" ⇒ Preview
    case "live" ⇒ Live
    case _ ⇒ throw new IllegalArgumentException(s"Not a valid env: $env. Allowed values are 'preview' and 'live'")
  }
}

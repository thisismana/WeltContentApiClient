package de.welt.contentapi.raw.client.services

import javax.inject.Inject

import de.welt.contentapi.core.client.services.s3.S3Client
import de.welt.contentapi.raw.models.RawChannel
import de.welt.contentapi.utils.Env.Env
import de.welt.contentapi.utils.Loggable
import play.api.{Configuration, Environment, Mode}
import play.api.cache.CacheApi
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.duration._

trait RawTreeService {
  def root(implicit env: Env): Option[RawChannel]
}

class RawTreeServiceImpl @Inject()(s3Client: S3Client,
                                   config: Configuration,
                                   environment: Environment,
                                   cache: CacheApi) extends RawTreeService with Loggable {

  val bucket = config.getString("welt.aws.s3.rawTree.bucket")
    .getOrElse(throw config.reportError("welt.aws.s3.rawTree.bucket", "welt.aws.s3.rawTree.bucket bucket not configured"))
  val file = config.getString("welt.aws.s3.rawTree.file")
    .getOrElse(throw config.reportError("welt.aws.s3.rawTree.file", "welt.aws.s3.rawTree.file file not configured"))

  // todo (all): let's talk about the folder structure
  protected def objectKeyForEnv(env: Env) = environment.mode match {
    case Mode.Prod ⇒ s"$file/prod/${env.toString}/config.json"
    case _ ⇒ s"$file/dev/${env.toString}/config.json"
  }

  // todo (mana): add metrics
  def root(implicit env: Env): Option[RawChannel] = {

    cache.getOrElse(s"rawChannelData-$env", 10.minutes) {
      s3Client.get(bucket, objectKeyForEnv(env)).flatMap { tree ⇒
        import de.welt.contentapi.raw.models.RawReads._
        Json.parse(tree).validate[RawChannel] match {
          case s: JsSuccess[RawChannel] ⇒
            log.info(s"Loaded/Refreshed raw tree for for $env")
            s.asOpt
          case e: JsError ⇒
            log.error(f"JsError parsing S3 file: '$bucket%s/$file%s'. " + JsError.toJson(e).toString())
            None
        }
      }
    }
  }
}

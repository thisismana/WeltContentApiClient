package de.welt.contentapi.pressed.client.services

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

import de.welt.contentapi.pressed.client.repository.{PressedDiggerClient, PressedS3Client}
import de.welt.contentapi.pressed.models.ApiPressedSection
import de.welt.contentapi.utils.Env.{Env, Live}

import scala.concurrent.{ExecutionContext, Future}

trait PressedSectionService {
  val ttlInMinutes: Long = 30

  def findByPath(path: String, env: Env = Live): Future[ApiPressedSection]
}

// Env == preview -> always Digger, no S3 first
case class PressedSectionServiceImpl @Inject()(pressedS3Client: PressedS3Client,
                                               diggerClient: PressedDiggerClient,
                                               executionContext: ExecutionContext) extends PressedSectionService {
  /** Primarily gets Pressed Section from S3, if pressed is older than 30 minutes or is not present -> fallback to digger rest call
    *
    * @param path path for section to be pressed
    * @param env can be Live or Preview, but is Live as default
    * @return a future ApiPressedSection or deliver HttpClient/ServerError from AbstractService
    */
  override def findByPath(path: String, env: Env = Live): Future[ApiPressedSection] =
    pressedS3Client
      .find(path)
      .flatMap { response ⇒
        if (response._2.plus(ttlInMinutes, ChronoUnit.MINUTES).isAfter(Instant.now())) {
          Some(response._1)
        } else {
          Option.empty
        }
      } match {
      case Some(s) ⇒ Future.successful(s)
      case _ ⇒ diggerClient.findByPath(path)
    }


}

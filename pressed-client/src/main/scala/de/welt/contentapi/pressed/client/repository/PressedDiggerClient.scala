package de.welt.contentapi.pressed.client.repository

import javax.inject.Inject

import com.kenshoo.play.metrics.Metrics
import de.welt.contentapi.core.client.services.configuration.{ContentClientConfig, ServiceConfiguration}
import de.welt.contentapi.core.client.services.contentapi.AbstractService
import de.welt.contentapi.pressed.models.ApiPressedSection
import de.welt.contentapi.utils.Env.{Env, Live}
import play.api.libs.json.{JsLookupResult, JsResult}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

sealed trait PressedDiggerClient {
  protected val serviceName = "digger"
  def findByPath(path: String, env: Env = Live): Future[ApiPressedSection]
}

case class PressedDiggerClientImpl @Inject()(override val ws: WSClient,
                                             override val metrics: Metrics,
                                             cfg: ContentClientConfig,
                                             implicit val executionContext: ExecutionContext) extends AbstractService[ApiPressedSection] with PressedDiggerClient {

  override def config: ServiceConfiguration = cfg.getServiceConfig(serviceName)

  import de.welt.contentapi.pressed.models.PressedReads.apiPressedSectionReads

  override def jsonValidate: (JsLookupResult) ⇒ JsResult[ApiPressedSection] = jsLookupResult ⇒
    jsLookupResult.validate[ApiPressedSection](apiPressedSectionReads)

  override def findByPath(path: String, env: Env = Live): Future[ApiPressedSection] = {
      // todo (harry): use env in path for digger, provide formatable 'endpoint'
      get(ids = Seq(env.toString, path), parameters = Nil, Nil)
  }

}

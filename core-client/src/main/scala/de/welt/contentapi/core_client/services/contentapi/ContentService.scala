package de.welt.contentapi.core_client.services.contentapi

import javax.inject.{Inject, Singleton}

import com.kenshoo.play.metrics.Metrics
import de.welt.contentapi.core.models.ApiResponse
import de.welt.contentapi.core_client.services.configuration.{ContentClientConfig, ServiceConfiguration}
import de.welt.contentapi.core_client.services.http.RequestHeaders
import de.welt.contentapi.utils.Loggable
import play.api.libs.json.{JsLookupResult, JsResult}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

trait ContentService {

  protected val serviceName = "content"

  def find(id: String, showRelated: Boolean = true)
          (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiResponse]
}

@Singleton
class ContentServiceImpl @Inject()(override val ws: WSClient,
                                   override val metrics: Metrics,
                                   funkConfig: ContentClientConfig)
  extends AbstractService[ApiResponse] with ContentService with Loggable {

  import de.welt.contentapi.core.models.ApiReads._

  override def jsonValidate: JsLookupResult ⇒ JsResult[ApiResponse] = _.validate[ApiResponse]

  override def config: ServiceConfiguration = funkConfig.getServiceConfig(serviceName)

  override def find(id: String, showRelated: Boolean)
                   (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[ApiResponse] = {

    val parameters = if (showRelated) {
      Seq("show-related" → "true")
    } else {
      Seq.empty
    }
    get(Seq(id), parameters, Nil)
  }
}


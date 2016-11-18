package de.welt.contentapi.core_client.services.contentapi

import javax.inject.{Inject, Singleton}

import com.kenshoo.play.metrics.Metrics
import de.welt.contentapi.core.models.ApiContent
import de.welt.contentapi.core_client.models.ApiContentSearch
import de.welt.contentapi.core_client.services.configuration.{ContentClientConfig, ServiceConfiguration}
import de.welt.contentapi.core_client.services.http.RequestHeaders
import play.api.libs.json.{JsLookupResult, JsResult}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

sealed trait ContentSearchService {
  protected val serviceName = "search"
  val defaultResultSize = 12
  val maxResultSize = 30

  def search(query: ApiContentSearch)
            (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[Seq[ApiContent]]


  /**
    * filter invalid values (such as negative or too large)
    *
    * @param maybeLimit the requested limit
    * @return sanitized limit:
    *         the value, if within valid bounds
    *         max allowed is `ContentSearchServiceImpl.maxResultSize`
    *         `ContentSearchServiceImpl.defaultResultSize` if `None` was passed
    */
  def sanitizeLimit(maybeLimit: Option[Int]) :Int = {
    maybeLimit.map(l => Math.abs(Math.min(l, maxResultSize))) getOrElse defaultResultSize
  }
}

@Singleton
class ContentSearchServiceImpl @Inject()(override val ws: WSClient,
                                         override val metrics: Metrics,
                                         cfg: ContentClientConfig)
  extends AbstractService[Seq[ApiContent]] with ContentSearchService {

  import de.welt.contentapi.core.models.ApiReads.apiContentReads

  override def config: ServiceConfiguration = cfg.getServiceConfig(serviceName)

  override def jsonValidate: (JsLookupResult) => JsResult[Seq[ApiContent]] = json => json.validate[Seq[ApiContent]]

  override def search(apiContentSearch: ApiContentSearch)
                     (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[Seq[ApiContent]] = {


    get(Nil, apiContentSearch.getAllParamsUnwrapped, Nil)
  }

}
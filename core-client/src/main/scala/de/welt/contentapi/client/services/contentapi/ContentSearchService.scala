package de.welt.contentapi.client.services.contentapi

import javax.inject.{Inject, Singleton}

import com.kenshoo.play.metrics.Metrics
import de.welt.contentapi.client.services.configuration.{ContentClientConfig, ServiceConfiguration}
import de.welt.contentapi.core.models.content.ApiContent
import de.welt.contentapi.core.models.internal.http.RequestHeaders
import de.welt.contentapi.core.models.resolve.contentapi.ApiContentSearch
import play.api.libs.json.{JsLookupResult, JsResult}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

sealed trait ContentSearchService {
  protected val serviceName = "search"

  def search(query: ApiContentSearch)
            (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[Seq[ApiEnrichedContent]]
}

@Singleton
class ContentSearchServiceImpl @Inject()(override val ws: WSClient,
                                         override val metrics: Metrics,
                                         cfg: ContentClientConfig,
                                         sectionService: SectionService)
  extends AbstractService[Seq[ApiContent]] with ContentSearchService {

  override def config: ServiceConfiguration = cfg.getServiceConfig(serviceName)

  override def jsonValidate: (JsLookupResult) => JsResult[Seq[ApiContent]] = json => json.validate[Seq[ApiContent]]

  override def search(apiContentSearch: ApiContentSearch)
                     (implicit requestHeaders: Option[RequestHeaders], executionContext: ExecutionContext): Future[Seq[ApiEnrichedContent]] = {


    get(Nil, apiContentSearch.getAllParamsUnwrapped, Nil).map { responses ⇒
      responses.map { content ⇒
        sectionService.enrich(content)
      }
    }
  }

}

object ContentSearchService {
  val defaultResultSize = 12
  val maxResultSize = 30

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

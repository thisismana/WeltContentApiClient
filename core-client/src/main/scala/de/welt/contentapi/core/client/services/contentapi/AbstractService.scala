package de.welt.contentapi.core.client.services.contentapi

import com.codahale.metrics.{MetricRegistry, Timer}
import com.kenshoo.play.metrics.Metrics
import de.welt.contentapi.core.client.services.configuration.ServiceConfiguration
import de.welt.contentapi.core.client.services.exceptions.{HttpClientErrorException, HttpServerErrorException}
import de.welt.contentapi.core.client.services.http.RequestHeaders
import de.welt.contentapi.utils.Loggable
import play.api.http.Status
import play.api.libs.json.{JsError, JsLookupResult, JsResult, JsSuccess}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest}
import play.api.mvc.Headers

import scala.concurrent.{ExecutionContext, Future}

trait AbstractService[T] extends Loggable with Status {

  /** these need to be provided by the implementing services */
  val ws: WSClient
  val metrics: Metrics

  /**
    * This must provide a [[ServiceConfiguration]]. It will be used to
    * configure the REST request.
    *
    * @return a [[ServiceConfiguration]]
    */
  def config: ServiceConfiguration

  /**
    * This must provide a function that maps a [[JsLookupResult]] to a [[JsResult]].
    * Overriding this outside this trait is required, since the macro-mechanism that
    * takes care of the JSON parsing does not work with generic types `T`
    *
    * @return a [[JsResult]]
    */
  def jsonValidate: JsLookupResult => JsResult[T]

  /**
    * @param ids string interpolation parameter for endpoint. e.g. /foo/%s/bar/%s
    * @param parameters
    * @param enforcedHeaders manually set headers for the GET request
    * @param forwardedRequestHeaders forwarded request headers from the controller e.g. API key
    * @param executionContext
    * @return
    */
  def get(ids: Seq[String] = Nil,
          parameters: Seq[(String, String)] = Nil,
          enforcedHeaders: RequestHeaders = Nil)
         (implicit forwardedRequestHeaders: Option[RequestHeaders] = None, executionContext: ExecutionContext): Future[T] = {

    def parseJson(json: JsLookupResult): T = jsonValidate(json) match {
      case JsSuccess(value, _) => value
      case err@JsError(_) => throw new IllegalStateException(err.toString)
    }

    val context = initializeMetricsContext(config.serviceName)

    val url: String = config.host + config.endpoint.format(ids: _*)

    val getRequest: WSRequest = ws.url(url)
      .withQueryString(parameters: _*)
      .withHeaders(enforcedHeaders ++ forwardHeaders(forwardedRequestHeaders): _*)
      .withAuth(config.username, config.password, WSAuthScheme.BASIC)

    log.debug(s"HTTP GET to ${getRequest.uri}")

    getRequest.get().map { response ⇒

      context.stop()

      response.status match {
        case OK ⇒ parseJson(response.json.result)
        case status if (400 until 500).contains(status) ⇒ throw HttpClientErrorException(status, response.statusText, url)
        case status ⇒ throw HttpServerErrorException(status, response.statusText, url)
      }
    }
  }

  /**
    * headers to be forwarded from client to server, e.g. the `X-Unique-Id`
    * @param maybeHeaders [[Headers]] from the incoming [[play.api.mvc.Request]]
    *
    * @return tuples of type String for headers to be forwarded
    */
  def forwardHeaders(maybeHeaders: Option[RequestHeaders]): RequestHeaders = {
    maybeHeaders.map(_.toMap[String, String]).flatMap(_.get("X-Unique-Id"))
      match {
        case Some(value) ⇒ Seq(("X-Unique-Id", value))
        case _ ⇒ Nil
      }
  }

  protected def initializeMetricsContext(name: String): Timer.Context = {
    metrics.defaultRegistry.timer(MetricRegistry.name(s"funkotron.$name", "requestTimer")).time()
  }
}

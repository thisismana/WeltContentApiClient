package de.welt.contentapi.core.client.services.contentapi

import akka.actor.Scheduler
import akka.pattern.CircuitBreaker
import com.codahale.metrics.{Gauge, MetricRegistry, Timer}
import com.kenshoo.play.metrics.Metrics
import de.welt.contentapi.core.client.services.CapiExecutionContext
import de.welt.contentapi.core.client.services.configuration.ServiceConfiguration
import de.welt.contentapi.core.client.services.exceptions.{HttpClientErrorException, HttpRedirectException, HttpServerErrorException}
import de.welt.contentapi.core.client.services.http.RequestHeaders
import de.welt.contentapi.utils.Strings
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsError, JsResult, JsSuccess}
import play.api.libs.ws.{BodyWritable, WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.Headers
import play.api.{Configuration, Logger}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

abstract class AbstractService[T](ws: WSClient,
                                  metrics: Metrics,
                                  configuration: Configuration,
                                  val serviceName: String,
                                  implicit val capi: CapiExecutionContext)
  extends Strings {

  /**
    * This must provide a [[ServiceConfiguration]]. It will be used to
    * configure the REST request.
    *
    * @return a [[ServiceConfiguration]]
    */
  protected[contentapi] val config: ServiceConfiguration = ServiceConfiguration(s"welt.api.$serviceName", configuration)

  /**
    * This must provide a function that maps a [[WSResponse]] to a [[Try]].
    * Overriding this outside this trait is required, since the macro-mechanism that
    * takes care of the response parsing does not work with generic types `T`
    * b/c of different content types such as XML or JSON
    *
    * @return a [[Try]]
    */
  def validate: WSResponse => Try[T]

  protected[contentapi] lazy val breaker: CircuitBreaker = AbstractService.circuitBreaker(capi.actorSystem.scheduler, config, serviceName)
  /**
    * `false` -> breaker will not open [this is a good exception, eg. 3xx and 4xx handled by custom-error-handler]
    * `true` -> breaker might open and prevent further requests for some time [this is bad]
    */
  private val circuitBreakerFailureFn: Try[T] ⇒ Boolean = {
    case Success(_) ⇒ false
    case Failure(_: HttpRedirectException) ⇒ false
    case Failure(_: HttpClientErrorException) ⇒ false
    case _ ⇒ true
  }
  /**
    * report the breaker state as a gauge to metrics, only if breaker is enabled
    */
  if (config.circuitBreaker.enabled) {
    Logger.info(s"Circuit Breaker enabled for $serviceName")
    // datadog gauges must be numeric
    metrics.defaultRegistry.register(s"service.$serviceName.circuit_breaker", new Gauge[Int]() {
      override def getValue: Int = breakerState()
    })
  } else {
    Logger.info(s"Circuit Breaker NOT enabled for $serviceName")
  }

  /**
    * this circuit breaker's current state
    *
    * @return `disabled` = -1
    *         `closed` = 0
    *         `half-open` = 1
    *         `open` = 2
    */
  def breakerState(): Int = if (config.circuitBreaker.enabled) {
    if (breaker.isClosed) {
      0
    } else if (breaker.isHalfOpen) {
      1
    } else {
      2
    }
  } else {
    -1
  }

  /**
    * @param urlArguments            string interpolation arguments for endpoint. e.g. /foo/%s/bar/%s see [[java.lang.String#format}]]
    * @param parameters              URL parameters to be sent with the request
    * @param headers                 optional http headers to be sent to the backend
    * @param body                    optional http body to be sent (e.g. for PUT and POST requests)
    * @param forwardedRequestHeaders forwarded request headers from the controller e.g. API key
    * @return
    */
  def execute[U: BodyWritable](urlArguments: Seq[String] = Nil,
                               parameters: Seq[(String, String)] = Nil,
                               headers: Seq[(String, String)] = Nil,
                               body: Option[U] = None)
                              (implicit forwardedRequestHeaders: RequestHeaders = Seq.empty): Future[T] = {

    val context = initializeMetricsContext(config.serviceName)

    val url: String = config.host + config.endpoint.format(urlArguments.map(stripWhiteSpaces).filter(_.nonEmpty): _*)

    val nonEmptyParameters = parameters.map { case (k, v) ⇒ k -> v.trim }.filter(_._2.nonEmpty)

    var request: WSRequest = ws.url(url)
      .withQueryStringParameters(nonEmptyParameters: _*)
      .addHttpHeaders(headers: _*)
      .addHttpHeaders(forwardHeaders(forwardedRequestHeaders): _*)

    body.foreach(b ⇒ {
      request = request.withBody(b)
    })

    request = config.credentials match {
      case Some(Right(basicAuth)) ⇒ request.withAuth(username = basicAuth._1, password = basicAuth._2, WSAuthScheme.BASIC)
      case Some(Left(apiKey)) ⇒ request.addHttpHeaders(AbstractService.HeaderApiKey → apiKey)
      case _ ⇒ request
    }

    Logger.debug(s"HTTP ${config.method} to ${request.uri}")

    lazy val block = request.execute(config.method).map { response ⇒
      context.stop()
      processResponse(response, request.url)
    }

    if (config.circuitBreaker.enabled) {
      breaker.withCircuitBreaker(block, circuitBreakerFailureFn)
    } else {
      block
    }
  }

  /**
    * headers to be forwarded from client to server, e.g. the `X-Unique-Id` or `X-Amzn-Trace-Id`
    *
    * @param maybeHeaders [[Headers]] from the incoming [[play.api.mvc.Request]]
    * @return tuples of type String for headers to be forwarded
    */
  private def forwardHeaders(maybeHeaders: RequestHeaders): RequestHeaders = {
    maybeHeaders.collect {
      case tuple@("X-Unique-Id", _) ⇒ tuple
      case tuple@("X-Amzn-Trace-Id", _) ⇒ tuple
    }
  }

  protected def initializeMetricsContext(name: String): Timer.Context = {
    metrics.defaultRegistry.timer(MetricRegistry.name(s"service.$name", "requestTimer")).time()
  }

  private def processResponse(response: WSResponse, url: String): T = response.status match {
    case Status.OK | Status.CREATED ⇒ processResult(response)
    case status if (300 until 400).contains(status) ⇒ throw HttpRedirectException(url, response.statusText)
    case status if (400 until 500).contains(status) ⇒
      throw HttpClientErrorException(status, response.statusText, url, response.header(HeaderNames.CACHE_CONTROL))
    case status ⇒ throw HttpServerErrorException(status, response.statusText, url)
  }

  private def processResult(result: WSResponse): T = validate(result) match {
    case Success(value) => value
    case Failure(err) => throw new IllegalStateException(err)
  }
}

object AbstractService {
  val HeaderApiKey = "x-api-key"

  def circuitBreaker(scheduler: Scheduler, config: ServiceConfiguration, serviceName: String)
                    (implicit ec: CapiExecutionContext): CircuitBreaker = new CircuitBreaker(
    scheduler,
    maxFailures = config.circuitBreaker.maxFailures,
    callTimeout = config.circuitBreaker.callTimeout,
    resetTimeout = config.circuitBreaker.resetTimeout)
    .withExponentialBackoff(config.circuitBreaker.exponentialBackoff)
    .onOpen(Logger.error(s"CircuitBreaker [$serviceName] is now open [this is bad], and will not close for some time"))
    .onClose(Logger.warn(s"CircuitBreaker [$serviceName] is now closed [this is good]"))
    .onHalfOpen(Logger.warn(s"CircuitBreaker [$serviceName] is now half-open [trying to recover]"))

  object implicitConversions {
    implicit def jsResult2Try[S](js: JsResult[S]): Try[S] = js match {
      case JsSuccess(value, _) ⇒ Success(value)
      case JsError(err) ⇒ Failure(new IllegalArgumentException(s"Cannot parse JSON ${err.mkString(",")}"))
    }
  }

}
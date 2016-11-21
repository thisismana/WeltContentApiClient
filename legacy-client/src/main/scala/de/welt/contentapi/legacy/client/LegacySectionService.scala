package de.welt.contentapi.legacy.client

import javax.inject.{Inject, Singleton}

import com.kenshoo.play.metrics.Metrics
import de.welt.contentapi.core_client.services.configuration.ServiceConfiguration
import de.welt.contentapi.core_client.services.contentapi.AbstractService
import de.welt.contentapi.core_client.services.http.RequestHeaders
import de.welt.contentapi.legacy.models.{ApiLegacyLists, ApiLegacyPressedSection, ApiLegacySection}
import de.welt.contentapi.pressed.client.services.PressedContentService
import play.api.libs.json.{JsLookupResult, JsResult}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

trait LegacySectionService {
  def getByPath(path: String)(implicit requestHeaders: Option[RequestHeaders] = None,
                              executionContext: ExecutionContext): Future[Seq[ApiLegacyPressedSection]]
}

@Singleton
class LegacySectionServiceImpl @Inject()(pressedContentService: PressedContentService,
                                         override val ws: WSClient,
                                         override val metrics: Metrics)
  extends AbstractService[ApiLegacyLists] with LegacySectionService {

  import de.welt.contentapi.legacy.models.LegacyFormats._

  override def getByPath(path: String)
                        (implicit requestHeaders: Option[RequestHeaders] = None,
                         executionContext: ExecutionContext): Future[Seq[ApiLegacyPressedSection]] = {

    super.get(ids = Seq(path)).map { _.unwrappedLists.map {convert} }
  }

  override def config: ServiceConfiguration = ???

  override def jsonValidate: (JsLookupResult) ⇒ JsResult[ApiLegacyLists] = json => json.validate[ApiLegacyLists]

  private def convert(apiLegacySection: ApiLegacySection): ApiLegacyPressedSection = ApiLegacyPressedSection(
    id = apiLegacySection.id,
    label = apiLegacySection.label,
    content = Option(apiLegacySection.unwrappedContent.map(apiContent ⇒ pressedContentService.convert(apiContent, None)))
  )
}

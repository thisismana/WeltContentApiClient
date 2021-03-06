package de.welt.contentapi.menu.services

import java.time.Instant

import com.google.inject.ImplementedBy
import de.welt.contentapi.core.client.services.CapiExecutionContext
import de.welt.contentapi.core.client.services.s3.S3Client
import de.welt.contentapi.menu.models.ApiMenu
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment, Logger, Mode}

@ImplementedBy(classOf[MenuServiceImpl])
trait MenuService {
  def get(): ApiMenu
  def refresh(): Unit = {}
}

@Singleton
class MenuServiceImpl @Inject()(config: Configuration,
                                s3: S3Client,
                                environment: Environment,
                                implicit val capi: CapiExecutionContext) extends MenuServiceRepository(config, environment.mode) with MenuService {

  private[services] var menu: ApiMenu = ApiMenu()
  private[services] var lastModified: Instant = Instant.MIN

  if (Mode.Test != environment.mode) {
    sync()
  } else {
    Logger.info("Menu Data will not be loaded when started in Mode.Test. Please mock it.")
  }

  override def get(): ApiMenu = menu

  override def refresh(): Unit = sync()

  private def sync() = {
    val maybeLastMod: Option[Instant] = s3.getLastModified(s3Config.bucket, s3Config.fullFilePath)
    Logger.info(s"Starting MenuService.sync(), LastMod=$maybeLastMod")

    for {
      remoteLastModified: Instant ← maybeLastMod
      if lastModified != remoteLastModified
    } yield {
      val maybeFreshMenu: Option[ApiMenu] = loadMenu(s3)

      maybeFreshMenu.foreach(freshMenu ⇒ {
        menu = freshMenu
        lastModified = remoteLastModified

        Logger.info(s"Finished MenuService.sync(). LastMod=$lastModified -> $remoteLastModified")
      })
    }
  }
}

package de.welt.contentapi.core.client.services.configuration

import java.io.File

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.profile.internal._
import com.amazonaws.auth.profile.internal.securitytoken.STSProfileCredentialsServiceLoader
import com.amazonaws.auth.{AWSCredentialsProvider, AWSCredentialsProviderChain, InstanceProfileCredentialsProvider}
import com.amazonaws.profile.path.AwsProfileFileLocationProvider
import com.amazonaws.regions.{Region, Regions}
import com.google.common.base.Stopwatch
import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}
import de.welt.contentapi.core.client.services.aws.ssm.ParameterStore
import de.welt.contentapi.utils.Loggable
import play.api.Configuration

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

case class ConfigurationException(message: String) extends RuntimeException(message)

object Environment extends Loggable {
  type Provider = Map[String, String]

  private[configuration] def extract(provider: Provider, key: String, default: String): String = provider.getOrElse(key, default)

  private[configuration] def extract(provider: Provider, key: String): Option[String] = provider.get(key)

  private val maybe_env_from_config_resource: Provider ⇒ String = provider ⇒
    extract(provider, "config.resource")
      .orElse(Some("application.conf"))
      .map(resourceName ⇒ ConfigFactory.parseResourcesAnySyntax(resourceName).resolve())
      .map(Configuration(_))
      .flatMap(config ⇒ config.getOptional[String]("content_api.mode"))
      .getOrElse(extract(sys.env, "MODE", "test"))
      .toLowerCase()

  protected[configuration] val parseVersionConfFile: Config ⇒ Try[(String, mutable.Buffer[String])] = rootConfig ⇒ {
    for {
      config ← Try(rootConfig.getConfig("build_info"))
      key ← Try(config.getString("module"))
      value ← Try(config.getList("dependencies").asScala.map(_.unwrapped())
        .collect { case s: String ⇒ s }.sorted)
        .orElse(Success(mutable.Buffer.empty[String]))
    } yield key → value
  }

  protected[configuration] val hasNoParent: Map[String, mutable.Buffer[String]] ⇒ Option[String] = foo ⇒ foo.find {
    case (name, _) ⇒ !foo.exists {
      case (_, dependencies) ⇒ dependencies.contains(name)
    }
  }.map(_._1)

  val stage: Mode = maybe_env_from_config_resource(System.getProperties.asScala.toMap) match {
    case "production" ⇒ Production
    case "staging" ⇒ Staging
    case "dev" ⇒ Development
    case _ ⇒ Test
  }

  val app: String = {
    if (!stage.isDev) {
      // every module should contain a `version.conf` from which we infer the running app
      Try(ConfigFactory.load("version.conf")
        .getConfig("build_info")
        .getString("module")
      ).getOrElse("local")
    } else {
      // all modules are flat in dev mode, so we need some logic here to figure out the current running app:
      val projectStructure = (getClass.getClassLoader.getResources("version.conf").asScala
        .map(res ⇒
          Try(ConfigFactory.parseURL(res)).flatMap(parseVersionConfFile)
        ) collect {
        case Success(value) ⇒ value
      }).toMap

      val currentModule = hasNoParent(projectStructure)
      // find the module that does not appear in any other module's dependencies (meaning: it has not parent)
      log.debug(s"active modules: ${projectStructure.keys.mkString(",")}. current module: $currentModule")
      currentModule.getOrElse("local")
    }
  }
  log.info(s"Environment loaded as: app=$app, stage=$stage")
}

/**
  * provide configuration required by the WCAPI itself
  *
  * - Services accessing AWS/S3
  * - API
  */
class ApiConfiguration extends Loggable {

  private lazy val awsRegion = Option(Regions.getCurrentRegion).getOrElse(Region.getRegion(Regions.EU_WEST_1))

  private def configFromResource(resourceName: String): Config = ConfigFactory.parseResourcesAnySyntax(resourceName).resolve()

  private def configFromFile(path: String): Config = ConfigFactory.parseFileAnySyntax(new File(path)).resolve()

  private def configFromParameterStore(path: String): Config = {
    val params = parameterStore.getPath(path)
    log.debug(s"[SSM] Loading config. path='$path'.")
    val configMap = params.map {
      // remove the path prefix from ssm
      case (key, value) ⇒ key.replaceFirst(path, "") -> value
    }
    ConfigFactory.parseMap(configMap.asJava, s"SSM param store @$path").resolve()
  }

  private lazy val parameterStore = new ParameterStore(awsRegion)

  import Environment._

  lazy val configuration: Config = {
    if (Environment.stage.isTest) {
      val testConfigResource = Environment.extract(System.getProperties.asScala.toMap, "config.resource")
        .getOrElse("application.test.conf")
      log.debug(s"Loading test configuration from $testConfigResource")
      val config = configFromResource(testConfigResource)
      log.debug(ApiConfiguration.configuration.root().render(ConfigRenderOptions.concise()))
      config
    } else {

      val sw = Stopwatch.createStarted()

      log.debug("Started loading Configuration.")
      val userPrivate = configFromFile(s"${System.getProperty("user.home")}/.welt/frontend.conf")
      val frontendConfig = configFromParameterStore("/frontend/")
      val frontendStageConfig = configFromParameterStore(s"/frontend/${stage.toString.toLowerCase()}/")
      val frontendAppConfig = configFromParameterStore(s"/frontend/${stage.toString.toLowerCase()}/${app.toLowerCase}/")

      val config = Try(userPrivate
        .withFallback(frontendAppConfig)
        .withFallback(frontendStageConfig)
        .withFallback(frontendConfig)
      )

      config match {
        case Success(value) ⇒
          log.debug(s"Finished loading Configuration in ${sw.stop().toString}.")
          log.debug(value.root().render(ConfigRenderOptions.concise()))
          value
        case Failure(exception) ⇒
          log.error("Could not load config.", exception)
          throw exception
      }
    }

  }

  def reportError(path: String, message: String, c: Config = configuration) = {
    val origin = Option(if (c.hasPath(path)) c.getValue(path).origin else c.root.origin)
      .map(o ⇒ s" (Cfg-Source: ${o.toString})")
      .getOrElse("")
    val ex = ConfigurationException(s"$message$origin")
    log.error(s"Configuration error.$origin", ex)
    throw ex
  }

  object aws {
    lazy val credentials: Option[AWSCredentialsProvider] = {
      log.debug("Providing aws credentials chain: profile[frontend] -> InstanceProfile")

      // ProfileAssumeRoleCredentialsProvider
      // issue: https://github.com/aws/aws-sdk-java/issues/803
      // workaround: https://gist.github.com/adrian-baker/81ec8e7cd8f8e15d343157ac9116faac
      val allProfiles = BasicProfileConfigLoader.INSTANCE.loadProfiles(
        AwsProfileFileLocationProvider.DEFAULT_CONFIG_LOCATION_PROVIDER.getLocation
      ).getProfiles.asScala.map { case (k, v) ⇒ k.replaceFirst("^profile ", "") → v } ++
        BasicProfileConfigLoader.INSTANCE.loadProfiles(
          AwsProfileFileLocationProvider.DEFAULT_CREDENTIALS_LOCATION_PROVIDER.getLocation
        ).getProfiles.asScala

      val frontendProfile = allProfiles.getOrElse("frontend", throw new RuntimeException("Profile 'frontend' not found."))
      val profiles = new AllProfiles(allProfiles.asJava)

      val profileCredentialsProvider = if (frontendProfile.isRoleBasedProfile) {
        new ProfileAssumeRoleCredentialsProvider(STSProfileCredentialsServiceLoader.getInstance(), profiles, frontendProfile)
      } else {
        new ProfileStaticCredentialsProvider(frontendProfile)
      }
      val provider = new AWSCredentialsProviderChain(
        profileCredentialsProvider,
        InstanceProfileCredentialsProvider.getInstance()
      )

      // this is a bit of a convoluted way to check whether we actually have credentials.
      // I guess in an ideal world there would be some sort of isConfigued() method...
      try {
        provider.getCredentials
        log.debug("Returning Provider")
        Some(provider)
      } catch {
        case ex: AmazonClientException =>
          log.error(ex.getMessage, ex)
          throw ex
      }
    }

    object s3 {

      lazy val region: String = configuration.getString("s3.region")

      object raw {
        lazy val bucket: String = configuration.getString("s3.raw_tree.bucket")
        lazy val file: String = configuration.getString("s3.raw_tree.file")
      }

      object author {
        lazy val bucket: String = configuration.getString("s3.author.bucket")
        lazy val file: String = configuration.getString("s3.author.file")
      }

    }

  }

}

// create one "static" instance of this configuration to be used by the capi
object ApiConfiguration extends de.welt.contentapi.core.client.services.configuration.ApiConfiguration
package de.welt.contentapi.core.client.services.aws.ssm

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.{GetParameterRequest, GetParametersByPathRequest}
import de.welt.contentapi.core.client.services.configuration.ApiConfiguration

import scala.annotation.tailrec
import scala.collection.JavaConverters._

class ParameterStore(region: Region) {

  private lazy val credentials: Option[AWSCredentialsProvider] = ApiConfiguration.aws.credentials
  private lazy val ssm = credentials.map { credentials ⇒
    AWSSimpleSystemsManagementClientBuilder
      .standard()
      .withCredentials(credentials)
      .withRegion(region.getName)
      .build()
  }.getOrElse(throw new RuntimeException("Failed to initialize AWSSimpleSystemsManagement"))

  def get(key: String): String = {
    val parameterRequest = new GetParameterRequest().withWithDecryption(true).withName(key)
    ssm.getParameter(parameterRequest).getParameter.getValue
  }

  def getPath(path: String): Map[String, String] = {

    @tailrec
    def pagination(acc: Map[String, String], nextToken: Option[String]): Map[String, String] = {
      val req = new GetParametersByPathRequest()
        .withPath(path)
        .withWithDecryption(true)
        .withRecursive(false)
      val reqWithToken = nextToken.map(req.withNextToken).getOrElse(req)

      val result = ssm.getParametersByPath(reqWithToken)

      val resultMap = acc ++ result.getParameters.asScala.map { param ⇒
        param.getName → param.getValue
      }

      Option(result.getNextToken) match {
        case Some(next) ⇒ pagination(resultMap, Some(next))
        case None ⇒ resultMap
      }
    }

    pagination(Map.empty, None)
  }
}
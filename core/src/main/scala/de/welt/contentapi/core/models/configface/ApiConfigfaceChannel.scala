package de.welt.contentapi.core.models.configface

import de.welt.contentapi.core.models.pressed._

case class ApiConfigfaceChannel(id: ApiChannelId,
                                meta: Option[ApiMetaConfiguration] = None,
                                commercial: Option[ApiCommercialConfiguration] = None,
                                branding: Option[ApiBrandingConfiguration] = None,
                                header: Option[ApiHeaderConfiguration] = None,
                                theme: Option[ApiThemeConfiguration] = None,

                                content: Option[ApiContentConfiguration] = None,

                                parent: Option[ApiConfigfaceChannel] = None,
                                children: Seq[ApiConfigfaceChannel] = Seq.empty) {

  def hasChildren = children.nonEmpty

  val DEFAULT_AD_TAG = "sonstiges"


}

case class ApiChannelId(path: String, eceId: Long = -1)

package de.welt.contentapi.raw.admin.client.services

import java.time.Instant

import com.google.common.base.Stopwatch
import com.google.inject.ImplementedBy
import de.welt.contentapi.core.client.services.CapiExecutionContext
import de.welt.contentapi.core.client.services.aws.s3.S3Client
import de.welt.contentapi.raw.client.services.{RawTreeService, RawTreeServiceImpl}
import de.welt.contentapi.raw.models.{ChannelUpdate, FullRawChannelWrites, RawChannel}
import de.welt.contentapi.utils.Loggable
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}

@ImplementedBy(classOf[AdminSectionServiceImpl])
trait AdminSectionService extends RawTreeService {

  def updateChannel(channel: RawChannel,
                    channelWithUpdates: RawChannel,
                    user: String): Option[RawChannel]

  /**
    * Syncs the current rawTree with the tree from Static Dump Provider to get new or changed Channels
    */
  def syncWithLegacy(): Unit

}

@Singleton
class AdminSectionServiceImpl @Inject()(s3: S3Client,
                                        legacySectionService: SdpSectionDataService,
                                        capiContext: CapiExecutionContext)
  extends RawTreeServiceImpl(s3, capiContext) with AdminSectionService with Loggable {

  override def updateChannel(channel: RawChannel, channelWithUpdates: RawChannel, user: String): Option[RawChannel] = {

    // update channel
    channel.config = channelWithUpdates.config
    // modify meta data
    channel.metadata = channel.metadata.copy(
      lastModifiedDate = Instant.now.toEpochMilli,
      changedBy = user
    )
    // update the stages/modules
    channel.stageConfiguration = channelWithUpdates.stageConfiguration

    log.info(s"$channel changed by $user")

    // save changes
    save

    // reload changes from s3
    val freshRootNode = root
    val updatedChannel = freshRootNode.flatMap(_.findByPath(channel.id.path))
    log.debug(s"Updated Channel from fresh s3 data $updatedChannel")
    updatedChannel
  }

  override def syncWithLegacy(): Unit = {
    def mergeAndSave(updates: RawChannel): Option[ChannelUpdate] = {
      val maybeRoot = root
      val mergeResult = maybeRoot.map(root => ChannelTools.merge(root, updates))
      maybeRoot.foreach(root ⇒ saveChannel(root))
      mergeResult
    }

    log.info(s"[Sync] starting sync. ")
    val stopwatch = Stopwatch.createStarted()
    val updates = legacySectionService.getSectionData.toChannel

    mergeAndSave(updates)
    val mergeUpdate = mergeAndSave(updates)

    mergeUpdate.foreach(update ⇒ log.info(s"[Sync] Done syncing with legacy, found the following changes: $update"))
    log.info(s"[Sync] took ${stopwatch.stop.toString}")
  }

  override def root: Option[RawChannel] = super.root
    .orElse(getTree.map(_.channel))
    .orElse {
      log.warn("No data found in s3 bucket, creating new data set from scratch.")
      val root = legacySectionService.getSectionData.toChannel

      saveChannel(root)
      Some(root)
    }

  private def saveChannel(ch: RawChannel) = {

    val json: JsValue = Json.toJson(ch)(FullRawChannelWrites.channelWrites)
    val serializedChannelData = json.toString

    log.info(s"saving channel tree to s3 -> $file")

    s3.putPrivate(bucket, file, serializedChannelData, "application/json")
  }

  def save = {
    root.foreach(r ⇒ saveChannel(r))
    log.debug("Invalidating cache.")
    update()
  }
}

object ChannelTools extends Loggable {

  def diff(current: RawChannel, update: RawChannel): ChannelUpdate = {

    if (current != update) {
      log.debug(s"Cannot diff($current, $update, because they are not .equal()")
      ChannelUpdate(Seq.empty, Seq.empty, Seq.empty)
    } else {

      val bothPresentIds = current.children.map(_.id).intersect(update.children.map(_.id))
      val updatesFromChildren = bothPresentIds.flatMap { id ⇒
        val tupleOfMatchingChannels = current.children.find(_.id == id).zip(update.children.find(_.id == id))

        tupleOfMatchingChannels.map { tuple ⇒
          diff(tuple._1, tuple._2)
        }
      }
      // elements that are no longer in `other.children`
      val deletedByOther = current.children.diff(update.children)
      // additional elements from `other.children`
      val addedByOther = update.children.diff(current.children)

      val moved = {

        // if we can find it in our tree, it hasn't been added but only moved
        val notAddedButMoved = addedByOther.flatMap { e ⇒ current.root.findByEscenicId(e.id.escenicId) }

        lazy val otherRoot = update.root
        // if we can find the deleted elem, it has been moved
        val notDeletedButMoved = deletedByOther.filter { elem ⇒ otherRoot.findByEscenicId(elem.id.escenicId).isDefined }

        notAddedButMoved ++ notDeletedButMoved
      }

      val deleted = deletedByOther.diff(moved)
      val added = addedByOther.diff(moved)

      val channelUpdate = ChannelUpdate(added, deleted, moved).merge(updatesFromChildren)
      if (!channelUpdate.isEmpty) {
        log.debug(s"[$this] Changes: $channelUpdate\n\n")
      }
      channelUpdate
    }
  }

  def merge(current: RawChannel, other: RawChannel): ChannelUpdate = {

    val channelUpdate = diff(current, other)

    channelUpdate.deleted.foreach { deletion ⇒
      deletion.parent.foreach { parent ⇒
        parent.children = parent.children.filterNot(_ == deletion)
      }
    }

    channelUpdate.added.foreach { addition ⇒
      current.children = current.children :+ addition
    }

    channelUpdate.moved.foreach { moved ⇒
      // remove from current parent
      moved.parent.foreach { parent ⇒
        parent.children = parent.children.filterNot(_ == moved)
      }
      // add to new parent
      val newParentId = other.findByEscenicId(moved.id.escenicId)
        .flatMap(_.parent)
        .map(_.id.escenicId)

      newParentId.foreach { parentId ⇒
        current.root.findByEscenicId(parentId).foreach { newParent ⇒
          newParent.children = newParent.children :+ moved
        }
      }
    }

    // update master data (path and displayName) from legacy source
    updateData(current, other)

    // for logging
    channelUpdate
  }

  /**
    * copies some data from the `legacyRoot` tree to the `current` tree
    *
    * @param current    the destination where to write updates to
    * @param legacyRoot the source object where to read updates from
    */
  def updateData(current: RawChannel, legacyRoot: RawChannel): Unit = {
    legacyRoot.findByEscenicId(current.id.escenicId).foreach(other ⇒ current.updateMasterData(other))
    current.children.foreach(child ⇒ updateData(child, legacyRoot))
  }


}





package com.ingbyr.vdm.dao

import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskStatus
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class DownloadTaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DownloadTaskDAO>(DownloadTaskTable)

    var taskConfig by TaskConfigDAO referencedOn DownloadTaskTable.taskConfig
    var checked by DownloadTaskTable.checked
    var title by DownloadTaskTable.title
    var size by DownloadTaskTable.size
    var status by DownloadTaskTable.status
    var progress by DownloadTaskTable.progress
    var createdAt by DownloadTaskTable.createdAt
}

/**
 * transfer DownloadTaskDAO to DownloadTaskModel
 */
fun DownloadTaskDAO.trans(): DownloadTaskModel = DownloadTaskModel(
        this.taskConfig.trans(),
        this.createdAt,
        this.checked,
        this.title,
        this.size,
        this.progress.toDouble(),
        DownloadTaskStatus.valueOf(this.status))
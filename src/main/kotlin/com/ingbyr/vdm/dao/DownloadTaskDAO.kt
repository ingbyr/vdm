package com.ingbyr.vdm.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class DownloadTaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DownloadTaskDAO>(DownloadTaskTable)

    var taskConfig by DownloadTaskTable.taskConfig
    var checked by DownloadTaskTable.checked
    var title by DownloadTaskTable.title
    var size by DownloadTaskTable.size
    var status by DownloadTaskTable.status
    var progress by DownloadTaskTable.progress
    var createdAt by DownloadTaskTable.createdAt
}
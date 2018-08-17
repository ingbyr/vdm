package com.ingbyr.vdm.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class TaskConfigDAO(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<TaskConfigDAO>(TaskConfigTable)

    var url by TaskConfigTable.url
    var downloadType by TaskConfigTable.downloadType
    var engineType by TaskConfigTable.engineType
    var downloadDefaultFormat by TaskConfigTable.downloadDefaultFormat
    var storagePath by TaskConfigTable.storagePath
    var cookie by TaskConfigTable.cookie
    var ffmpeg by TaskConfigTable.ffmpeg
    var formatId by TaskConfigTable.formatID
    var proxyType by TaskConfigTable.proxyType
    var proxyAddress by TaskConfigTable.proxyAddress
    var proxyPort by TaskConfigTable.proxyPort
}
package com.ingbyr.vdm.dao

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column

object DownloadTaskTable : IntIdTable() {
    val taskConfig: Column<Int> = integer("taskConfig")
    val checked: Column<Boolean> = bool("checked").default(false)
    val title: Column<String> = varchar("title", length = 255)
    val size: Column<String> = varchar("size", length = 50)
    val status: Column<String> = varchar("status", length = 255)
    val progress: Column<Float> = float("progress").default(0f)
    val createdAt: Column<String> = varchar("createdAt", length = 50).uniqueIndex()
}
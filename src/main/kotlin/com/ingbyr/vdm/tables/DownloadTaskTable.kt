package com.ingbyr.vdm.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object DownloadTaskTable : Table() {
    val id: Column<String> = varchar("id", length = 50).primaryKey()
    val taskEngineConfig: Column<Int> = integer("taskEngineConfig")
    val url: Column<String> = varchar("url", length = 255)
    val checked: Column<Boolean> = bool("checked").default(false)
    val title: Column<String> = varchar("title", length = 255)
    val size: Column<String> = varchar("size", length = 50)
    val status: Column<String> = varchar("status", length = 255)
    val progress: Column<Float> = float("progress").default(0f)
    val createdAt: Column<String> = varchar("createdAt", length = 50)
    val formatID: Column<String> = varchar("formatID", length = 50)
    val type: Column<String> = varchar("type", length = 50)
}
package com.ingbyr.vdm.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TaskEngineConfigTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val engineType: Column<String> = varchar("engineType", 50)
    val proxyType: Column<String> = varchar("proxyType", length = 50)
    val proxyAddress: Column<String> = varchar("proxyAddress", length = 50)
    val proxyPort: Column<String> = varchar("proxyPort", length = 50)
    val downloadDefaultFormat: Column<Boolean> = bool("downloadDefaultFormat")
    val storagePath: Column<String> = varchar("storagePath", 255)
    val cookie: Column<String> = varchar("cookie", 255)
    val ffmpeg: Column<String> = varchar("ffmpeg", 255)
}
package com.ingbyr.vdm.dao

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column

object TaskConfigTable : IntIdTable() {
    val url: Column<String> = varchar("url", length = 255)
    val downloadType: Column<String> = varchar("downloadType", length = 50)
    val engineType: Column<String> = varchar("engineType", 50)
    val downloadDefaultFormat: Column<Boolean> = bool("downloadDefaultFormat")
    val storagePath: Column<String> = varchar("storagePath", 255)
    val cookie: Column<String> = varchar("cookie", 255)
    val ffmpeg: Column<String> = varchar("ffmpeg", 255)
    val formatID: Column<String> = varchar("formatID", length = 50).default("-1")
    val proxyType: Column<String> = varchar("proxyType", length = 50)
    val proxyAddress: Column<String> = varchar("proxyAddress", length = 50)
    val proxyPort: Column<String> = varchar("proxyPort", length = 50)
}
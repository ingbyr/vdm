package com.ingbyr.vdm.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.math.max

object VDMUtils {
    const val VDM_UPDATE_URL = "https://github.com/ingbyr/VDM/releases/latest"
    const val VDM_SOURCE_CODE = "https://github.com/ingbyr/VDM"
    const val VDM_LICENSE = "https://raw.githubusercontent.com/ingbyr/VDM/master/LICENSE.txt"
    const val VDM_REPORT_BUGS = "https://github.com/ingbyr/VDM/issues"
    const val UNKNOWN_VERSION = "0.0.0"
    const val DONATION_URL = "https://paypal.me/ingbyr"
    const val DB_DOWNLOAD_TASKS = "DB_DOWNLOAD_TASKS"

    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    val APP_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath()!!
    val USER_DIR = Paths.get(System.getProperty("user.home"), ".vdm").toAbsolutePath()!!
    val DATABASE_PATH_STR = Paths.get(System.getProperty("user.home"), ".vdm", "vdm.db").toAbsolutePath().toString()

    fun newVersion(localVersion: String, remoteVersion: String): Boolean {
        try {
            val lv: List<Int> = localVersion.split(".").map { it.toInt() }
            val rv: List<Int> = remoteVersion.split(".").map { it.toInt() }
            val count = max(lv.size, rv.size)
            for (i in 0 until count) {
                if (rv.getOrElse(i, { 0 }) - lv.getOrElse(i, { 0 }) > 0) return true
                else if (rv.getOrElse(i, { 0 }) - lv.getOrElse(i, { 0 }) < 0) return false
            }
        } catch (e: NumberFormatException) {
            logger.error(e.toString())
        }
        return false
    }
}
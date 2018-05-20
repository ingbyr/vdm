package com.ingbyr.vdm.utils

import java.nio.file.Paths
import kotlin.math.max

object VDMUtils {
    const val APP_UPDATE_URL = "https://github.com/ingbyr/GUI-YouGet/releases/latest"
    const val APP_SOURCE_CODE = "https://github.com/ingbyr/GUI-YouGet"
    const val APP_LICENSE = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt"
    const val APP_AUTHOR = "http://www.ingbyr.com/"
    const val APP_REPORT_BUGS = "https://github.com/ingbyr/GUI-YouGet/issues"
    const val APP_VERSION = "APP_VERSION"
    const val DONATION_URL = "https://paypal.me/ingbyr"
    const val DB_DOWNLOAD_TASKS = "DB_DOWNLOAD_TASKS"

    val APP_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath()!!
    val USER_DIR = Paths.get(System.getProperty("user.home"), ".vdm").toAbsolutePath()!!
    val DATABASE_PATH_STR = Paths.get(System.getProperty("user.home"), ".vdm", "vdm.db").toAbsolutePath().toString()

    fun newVersion(localVersion: String, remoteVersion: String): Boolean {
        val lv: List<Int> = localVersion.split(".").map { it.toInt() }
        val rv: List<Int> = remoteVersion.split(".").map { it.toInt() }
        val count = max(lv.size, rv.size)
        for (i in 0 until count) {
            if ((rv.getOrNull(i) ?: 0) - (lv.getOrNull(i) ?: 0) > 0) return true
            else if ((rv.getOrNull(i) ?: 0) - (lv.getOrNull(i) ?: 0) < 0) return false
        }
        return false
    }
}
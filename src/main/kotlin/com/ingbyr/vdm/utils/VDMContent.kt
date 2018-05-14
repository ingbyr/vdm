package com.ingbyr.vdm.utils

import java.nio.file.Paths

object VDMContent {
    const val APP_UPDATE_URL = "https://github.com/ingbyr/GUI-YouGet/releases/latest"
    const val APP_SOURCE_CODE = "https://github.com/ingbyr/GUI-YouGet"
    const val APP_LICENSE = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt"
    const val APP_AUTHOR = "http://www.ingbyr.com/"
    const val APP_REPORT_BUGS = "https://github.com/ingbyr/GUI-YouGet/issues"
    const val APP_VERSION = "APP_VERSION"
    const val NONE = "NONE"
    val APP_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath()!!
    val USER_DIR = Paths.get(System.getProperty("user.home"), ".vdm").toAbsolutePath()!!
    val DATABASE_PATH_STR = Paths.get(System.getProperty("user.home"), ".vdm", "vdm.db").toAbsolutePath().toString()
    val DB_DOWNLOAD_TASKS = "DB_DOWNLOAD_TASKS"
}
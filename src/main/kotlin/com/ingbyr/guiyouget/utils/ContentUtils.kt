package com.ingbyr.guiyouget.utils

object CommonUtils {
    const val STORAGE_PATH = "STORAGE_PATH"
    const val APP_VERSION = "version"
    const val APP_UPDATE_URL = "https://github.com/ingbyr/GUI-YouGet/releases/latest"
    const val APP_SOURCE_CODE = "https://github.com/ingbyr/GUI-YouGet"
    const val APP_LICENSE = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt"
    const val APP_AUTHOR = "http://www.ingbyr.com/"
    const val APP_REPORT_BUGS = "https://github.com/ingbyr/GUI-YouGet/issues"

    fun yougetUpdateURL(ver: String) = "https://github.com/soimort/you-get/releases/download/v$ver/you-get-$ver-win32.exe"

    fun youtubedlUpdateURL(ver: String) = "https://github.com/rg3/youtube-dl/releases/download/$ver/youtube-dl.exe"
}
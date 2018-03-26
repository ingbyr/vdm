package com.ingbyr.guiyouget.utils

object ContentsUtil {
//    const val DOWNLOAD_CORE = "download-engine"
//    const val REMOTE_YOU_GET_VERSION = "https://raw.githubusercontent.com/soimort/you-get/master/src/you_get/version.py"
//    const val REMOTE_YOUTUBE_DL_VERSION = "https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py"
//    const val YOUTUBE_DL = "youtube-dl"
//    const val YOUTUBE_DL_VERSION = "youtube-dl-version"
//    const val YOU_GET = "you-get"
//    const val YOU_GET_VERSION = "you-get-version"
    const val STORAGE_PATH = "storage-path"

//    const val PROXY_TYPE = "proxy-type"
//    const val PROXY_ADDRESS = "proxy-address"
//    const val PROXY_PORT = "proxy-port"
//    const val PROXY_HTTP = "http"
//    const val PROXY_SOCKS = "socks5"
//
//    const val NONE = ""

    const val APP_VERSION = "version"
    const val APP_UPDATE_URL = "https://github.com/ingbyr/GUI-YouGet/releases/latest"
    const val APP_SOURCE_CODE = "https://github.com/ingbyr/GUI-YouGet"
    const val APP_LICENSE = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt"
    const val APP_AUTHOR = "http://www.ingbyr.com/"
    const val APP_REPORT_BUGS = "https://github.com/ingbyr/GUI-YouGet/issues"

    fun yougetUpdateURL(ver: String) = "https://github.com/soimort/you-get/releases/download/v$ver/you-get-$ver-win32.exe"

    fun youtubedlUpdateURL(ver: String) = "https://github.com/rg3/youtube-dl/releases/download/$ver/youtube-dl.exe"
}
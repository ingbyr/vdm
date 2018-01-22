package com.ingbyr.guiyouget.utils

object ContentsUtil {
    val DOWNLOAD_CORE = "download-engine"
    val REMOTE_YOU_GET_VERSION = "https://raw.githubusercontent.com/soimort/you-get/master/src/you_get/version.py"
    val REMOTE_YOUTUBE_DL_VERSION = "https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py"
    val YOUTUBE_DL = "youtube-dl"
    val YOUTUBE_DL_VERSION = "youtube-dl-version"
    val YOU_GET = "you-get"
    val YOU_GET_VERSION = "you-get-version"
    val STORAGE_PATH = "storage-path"

    val PROXY_TYPE = "proxy-type"
    val PROXY_ADDRESS = "proxy-address"
    val PROXY_PORT = "proxy-port"
    val PROXY_HTTP = "http"
    val PROXY_SOCKS = "socks5"

    val APP_VERSION = "version"
    val APP_UPDATE_URL = "https://github.com/ingbyr/GUI-YouGet/releases/latest"
    val APP_SOURCE_CODE = "https://github.com/ingbyr/GUI-YouGet"
    val APP_LICENSE = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt"
    val APP_AUTHOR = "http://www.ingbyr.com/"
    val APP_REPORT_BUGS = "https://github.com/ingbyr/GUI-YouGet/issues"

    fun yougetUpdateURL(ver: String) = "https://github.com/soimort/you-get/releases/download/v$ver/you-get-$ver-win32.exe"

    fun youtubedlUpdateURL(ver: String) = "https://github.com/rg3/youtube-dl/releases/download/$ver/youtube-dl.exe"
}
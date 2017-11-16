package com.ingbyr.guiyouget.utils

object CoreUtils {
    var DOWNLOAD_CORE = "download-core"
    var REMOTE_CONF_URL = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/RemoteConf.json"
    val YOUTUBE_DL = "youtube-dl"
    val YOUTUBE_DL_VERSION = "youtube-dl-version"
    val YOU_GET = "you-get"
    val YOU_GET_VERSION = "you-get-version"
    val STORAGE_PATH = "storagePath"

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
    val APP_DONATE = "http://wx3.sinaimg.cn/large/bca3b20dly1fjx6l6j4r2j20ci0cgq55.jpg"

    fun yougetUpdateURL(ver: String) = "https://github.com/soimort/you-get/releases/download/v$ver/you-get-$ver-win32.exe"

    fun youtubedlUpdateURL(ver: String) = "https://github.com/rg3/youtube-dl/releases/download/$ver/youtube-dl.exe"
}
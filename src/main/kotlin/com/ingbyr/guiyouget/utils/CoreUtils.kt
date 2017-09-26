package com.ingbyr.guiyouget.utils

object CoreUtils {
    lateinit var current: String
    var REMOTE_CONF_URL = "https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/RemoteConf.json"
    val YOUTUBE_DL = "youtube-dl"
    val YOUTUBE_DL_VERSION = "youtube-dl-version"
    val YOU_GET = "you-get"
    val YOU_GET_VERSION = "you-get-version"

    val APP_VERSION = "version"

    fun yougetUpdateURL(ver: String) = "https://github.com/soimort/you-get/releases/download/v$ver/you-get-$ver-win32.exe"

    fun youtubedlUpdateURL(ver: String) = "https://github.com/rg3/youtube-dl/releases/download/$ver/youtube-dl.exe"
}
package com.ingbyr.vdm.utils

import java.nio.file.Paths

object AppProperties {
    // config file
    const val FIRST_TIME_USE = "FIRST_TIME_USE"
    const val STORAGE_PATH = "STORAGE_PATH"
    const val FFMPEG_PATH = "FFMPEG_PATH"
    const val DOWNLOAD_DEFAULT_FORMAT = "DOWNLOAD_DEFAULT_FORMAT"
    const val ENGINE_TYPE = "ENGINE_TYPE"
    const val PROXY_TYPE = "PROXY_TYPE"
    const val SOCKS5_PROXY_ADDRESS = "SOCKS5_PROXY_ADDRESS"
    const val SOCKS5_PROXY_PORT = "SOCKS5_PROXY_PORT"
    const val HTTP_PROXY_ADDRESS = "HTTP_PROXY_ADDRESS"
    const val HTTP_PROXY_PORT = "HTTP_PROXY_PORT"
    const val COOKIE = "COOKIE"
    const val DEBUG_MODE = "DEBUG_MODE"
    const val VDM_VERSION = "VDM_VERSION"
    const val YOUTUBE_DL_VERSION = "YOUTUBE_DL_VERSION"
    const val YOU_GET_VERSION = "YOU_GET_VERSION"
    const val THEME = "THEME"
    const val CHARSET = "CHARSET"

    // app content
    const val VDM_UPDATE_URL = "https://github.com/ingbyr/VDM/releases/latest"
    const val VDM_SOURCE_CODE = "https://github.com/ingbyr/VDM"
    const val VDM_LICENSE = "https://raw.githubusercontent.com/ingbyr/VDM/master/LICENSE.txt"
    const val VDM_REPORT_BUGS = "https://github.com/ingbyr/VDM/issues"
    const val UNKNOWN_VERSION = "0.0.0"
    const val DONATION_URL = "https://paypal.me/ingbyr"

    // path config
    val APP_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath()!!
    val USER_DIR = Paths.get(System.getProperty("user.home"), ".vdm").toAbsolutePath()!!

    // database
    val DATABASE_URL ="jdbc:h2:${Paths.get(System.getProperty("user.home"), ".vdm", "vdm").toAbsolutePath()}"
}
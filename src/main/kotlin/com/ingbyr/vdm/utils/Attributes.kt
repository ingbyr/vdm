package com.ingbyr.vdm.utils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Attributes {

    // path config
    val APP_DIR: Path = Paths.get(System.getProperty("user.dir"))
    val ENGINES_DIR: Path = APP_DIR.resolve("engines")
    val CONFIG_DIR: Path = APP_DIR.resolve("configs")
    val DB_DIR: Path = APP_DIR.resolve("db")
    val TMP_DIR: Path = APP_DIR.resolve("tmp")
    val configFilePath: Path = CONFIG_DIR.resolve("vdm.properties")
    val engineConfigFilePath: Path = CONFIG_DIR.resolve("engines.properties")

    init {
        Files.createDirectories(CONFIG_DIR)
        Files.createDirectories(ENGINES_DIR)
        Files.createDirectories(DB_DIR)
        Files.createDirectories(TMP_DIR)
    }

    // database
    val DATABASE_URL = "jdbc:h2:${DB_DIR.resolve("vdm")}"


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
    const val ENABLE_COOKIE = "ENABLE_COOKIE"
    const val CURRENT_COOKIE = "CURRENT_COOKIE"
    const val DEBUG_MODE = "DEBUG_MODE"
    const val VDM_VERSION = "VDM_VERSION"
    const val THEME = "THEME"
    const val CHARSET = "CHARSET"
    const val THEME_PRIMARY_COLOR = "THEME_PRIMARY_COLOR"
    const val THEME_SECONDARY_COLOR = "THEME_SECONDARY_COLOR"
    // app content
    const val VDM_UPDATE_URL = "https://github.com/ingbyr/VDM/releases/latest"
    const val VDM_SOURCE_CODE = "https://github.com/ingbyr/VDM"
    const val VDM_LICENSE = "https://raw.githubusercontent.com/ingbyr/VDM/master/LICENSE.txt"
    const val VDM_REPORT_BUGS = "https://github.com/ingbyr/VDM/issues"
    const val UNKNOWN_VERSION = "0.0.0"
    const val DONATION_URL = "https://paypal.me/ingbyr"

    // youtube-dl
    const val YOUTUBE_DL_VERSION = "YOUTUBE_DL_VERSION"

    // annie
    const val ANNIE_VERSION = "ANNIE_VERSION"
}
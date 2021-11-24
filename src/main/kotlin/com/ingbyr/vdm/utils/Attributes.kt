package com.ingbyr.vdm.utils

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.ProxyType
import javafx.scene.paint.Color
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Attributes {
    // path app
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

    // Default value for above
    object Defaults {
        val STORAGE_PATH: String = Attributes.APP_DIR.toString()
        val FFMPEG_PATH: String = "" // todo ffmpeg
        const val DOWNLOAD_DEFAULT_FORMAT: Boolean = true
        val ENGINE_TYPE: EngineType = EngineType.YOUTUBE_DL
        const val ENGINE_VERSION: String = "0.0.0"
        const val SOCKS5_PROXY_ADDRESS: String = ""
        const val SOCKS5_PROXY_PORT: String = ""
        const val HTTP_PROXY_ADDRESS: String = ""
        const val HTTP_PROXY_PORT: String = ""
        val PROXY_TYPE: ProxyType = ProxyType.NONE
        const val ENABLE_COOKIE: Boolean = false
        const val COOKIE: String = ""
        val CHARSET: String = Charsets.UTF_8.name()
        const val FIRST_TIME_USE: Boolean = false
        val THEME_PRIMARY_COLOR: Color = c("#263238")
        val THEME_SECONDARY_COLOR: Color = c("#455A64")
        const val DEBUG_MODE: Boolean = false
    }
}

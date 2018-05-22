package com.ingbyr.vdm.utils

import tornadofx.*


/**
 * Load and update the config file. It's necessary to invoke saveToConfigFile().
 */
class VDMConfigUtils(private val config: ConfigProperties) {

    companion object {
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
    }

    fun load(key: String): String {
        return config.string(key)
    }

    fun safeLoad(key: String, defaultValue: Any): String {
        return try {
            config.string(key)
        } catch (e: IllegalStateException) {
            config[key] = defaultValue.toString()
            defaultValue.toString()
        }
    }

    fun update(key: String, value: Any) {
        config[key] = value.toString()
    }

    fun saveToConfigFile() {
        config.save()
    }
}
package com.ingbyr.vdm.utils

import tornadofx.*


/**
 * Load and update the config file. It's necessary to invoke saveToConfigFile().
 */
class AppConfigUtils(private val config: ConfigProperties) {

    companion object {
        val configFilePath = AppProperties.USER_DIR.resolve("vdm.properties")!!
    }

    fun load(key: String): String {
        return config.string(key)
    }

    fun safeLoad(key: String, defaultValue: Any): String {
        return try {
            config.string(key)
        } catch (e: IllegalStateException) {
            config[key] = defaultValue.toString()
            config.save()
            defaultValue.toString()
        }
    }

    fun update(key: String, value: Any) {
        config[key] = value.toString()
        config.save()
    }
}
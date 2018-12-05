package com.ingbyr.vdm.utils

import tornadofx.*
import java.nio.file.Path


abstract class BaseConfigUtil : Configurable {
    override val configPath: Path = Attributes.configFilePath
    override val config: ConfigProperties by lazy { loadConfig() }

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

/**
 * Load and update the vdm.properties config file.
 */
object ConfigUtils : BaseConfigUtil()


object EngineConfigUtils : BaseConfigUtil() {
    override val configPath: Path = Attributes.engineConfigFilePath
}
package com.ingbyr.vdm.utils

import tornadofx.*


/**
 * Load and update the config file. It's necessary to invoke saveToConfigFile().
 */
object ConfigUtils : Controller(){

    private val c = app.config

    fun load(key: String): String {
        return c.string(key)
    }

    fun safeLoad(key: String, defaultValue: Any): String {
        return try {
            c.string(key)
        } catch (e: IllegalStateException) {
            c[key] = defaultValue.toString()
            c.save()
            defaultValue.toString()
        }
    }

    fun update(key: String, value: Any) {
        c[key] = value.toString()
        c.save()
    }
}
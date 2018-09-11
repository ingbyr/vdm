package com.ingbyr.vdm.controllers

import tornadofx.*

open class ConfigController : Controller() {

    fun loadConfig(key: String, defaultValue: Any = ""): String {
        return try {
            app.config.string(key)
        } catch (e: IllegalStateException) {
            app.config[key] = defaultValue.toString()
            app.config.save()
            defaultValue.toString()
        }
    }
    fun updateConfig(key: String, value: Any) {
        app.config[key] = value.toString()
        app.config.save()
    }
}
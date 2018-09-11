package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.UpdateEngineTask
import com.ingbyr.vdm.utils.AppConfigUtils
import com.ingbyr.vdm.utils.AppProperties
import com.ingbyr.vdm.utils.DebugUtils
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class PreferencesController : Controller() {

    val debugModeProperty = SimpleBooleanProperty()
    var debugMode: Boolean by debugModeProperty
    var cookieList = mutableListOf<String>().observable()
    val cookieProperty = SimpleStringProperty()
    private var cookie by cookieProperty

    private val cu = AppConfigUtils(app.config)

    init {
        initDebugMode()
        freshCookieListAndContent()
    }

    private fun initDebugMode() {
        debugMode = cu.safeLoad(AppProperties.DEBUG_MODE, "false").toBoolean()
        debugModeProperty.addListener { _, _, mode ->
            DebugUtils.changeDebugMode(mode)
            cu.update(AppProperties.DEBUG_MODE, mode)
        }
    }

    fun freshCookieListAndContent() {
        cookieList.clear()
        AppProperties.COOKIES_DIR.toFile().walkTopDown().filter { it.name.endsWith(".txt") }.forEach { cookieList.add(it.name) }
        readCookieContent()
    }

    fun readCookieContent() {
        val cookieName = cu.safeLoad(AppProperties.CURRENT_COOKIE, "")
        if (cookieName.isBlank()) cookie=  ""
        val cookieFile = AppProperties.COOKIES_DIR.resolve(cookieName).toFile()
        cookie = if (cookieFile.exists()) {
            cookieFile.readText()
        } else {
            ""
        }
    }

    fun updateEngine(engineType: EngineType, localVersion: String) {
        fire(UpdateEngineTask(engineType, localVersion))
    }
}
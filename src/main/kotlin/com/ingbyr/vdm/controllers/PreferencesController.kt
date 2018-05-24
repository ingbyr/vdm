package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engine.utils.EngineType
import com.ingbyr.vdm.events.UpdateEngineTask
import tornadofx.*
import java.util.*

class PreferencesController : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    fun updateEngine(engineType: EngineType, localVersion: String) {
        fire(UpdateEngineTask(engineType, localVersion))
    }
}
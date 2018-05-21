package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.events.UpdateEngineTask
import com.ingbyr.vdm.utils.EngineType
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
package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engine.EngineFactory
import com.ingbyr.vdm.utils.EngineType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class PreferencesController : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun updateEngine(engineType: EngineType) {
        logger.debug("[$engineType] check for updates")
        val engine = EngineFactory.create(engineType)

    }
}
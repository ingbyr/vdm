package com.ingbyr.vdm.controllers


import com.ingbyr.vdm.utils.ConfigUtils
import com.ingbyr.vdm.utils.Attributes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*

class WizardController : Controller() {
    private val logger: Logger = LoggerFactory.getLogger(WizardController::class.java)

    init {

    }

    fun updateThemeColor(color: String) {
        ConfigUtils.update(Attributes.THEME_COLOR, color)
    }
}
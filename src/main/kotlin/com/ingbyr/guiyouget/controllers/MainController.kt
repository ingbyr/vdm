package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.utils.CommonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*


class MainController : Controller() {
    private val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    fun updateGUI() {
        hostServices.showDocument(CommonUtils.APP_UPDATE_URL)
    }
}
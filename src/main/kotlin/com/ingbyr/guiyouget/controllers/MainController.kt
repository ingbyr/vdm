package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.utils.ContentUtils
import tornadofx.Controller


class MainController : Controller() {
    fun updateGUI() {
        hostServices.showDocument(ContentUtils.APP_UPDATE_URL)
    }
}
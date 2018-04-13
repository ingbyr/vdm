package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.utils.CommonUtils
import tornadofx.Controller


class MainController : Controller() {
    fun updateGUI() {
        hostServices.showDocument(CommonUtils.APP_UPDATE_URL)
    }
}
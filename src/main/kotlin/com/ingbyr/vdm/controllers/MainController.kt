package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.utils.ContentUtils
import tornadofx.Controller


class MainController : Controller() {
    fun updateGUI() {
        hostServices.showDocument(ContentUtils.APP_UPDATE_URL)
    }
}
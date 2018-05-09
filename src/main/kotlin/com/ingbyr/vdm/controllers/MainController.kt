package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.utils.VDMContent
import tornadofx.Controller


class MainController : Controller() {
    fun updateGUI() {
        hostServices.showDocument(VDMContent.APP_UPDATE_URL)
    }
}
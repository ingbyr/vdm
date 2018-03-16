package com.ingbyr.guiyouget.engine

import tornadofx.*
import java.util.*


abstract class DownloadEngineController : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/engine")
    }
    abstract fun request(args: MutableList<String>): StringBuilder
    abstract fun download(formatID: String)
    abstract fun parseStatus(line: String)
}
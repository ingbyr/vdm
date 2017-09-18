package com.ingbyr.guiyouget.controllers

import tornadofx.*
import java.nio.file.Paths

class MainController : Controller() {
    val storagePath = config.string("storagePath", Paths.get("").toAbsolutePath().toString())

    fun saveStoragePath(strPath: String) {
        config["storagePath"] = strPath
        config.save()
    }

    fun updateCore() {
        // TODO: check updates
        // https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py
    }
}
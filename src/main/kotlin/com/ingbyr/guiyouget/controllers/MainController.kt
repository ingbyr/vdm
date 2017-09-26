package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.core.YouGet
import com.ingbyr.guiyouget.core.YoutubeDL
import com.ingbyr.guiyouget.events.RequestMediasWithYouGet
import com.ingbyr.guiyouget.events.RequestMediasWithYoutubeDL
import com.ingbyr.guiyouget.utils.CoreUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*


class MainController : Controller() {
    private val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    fun requestMediaInfo(url: String) {
        val core = app.config["core"] as String
        logger.debug("download core is $core")

        // Init the request args
        when (core) {
            CoreUtils.YOUTUBE_DL -> {
                fire(RequestMediasWithYoutubeDL(YoutubeDL(url)))
            }

            CoreUtils.YOU_GET -> {
                fire(RequestMediasWithYouGet(YouGet(url)))
            }

            else -> {
                logger.error("bad downloading core $core")
            }
        }

    }


    fun updateGUI() {
        hostServices.showDocument("https://github.com/ingbyr/GUI-YouGet/releases/latest")
    }
}
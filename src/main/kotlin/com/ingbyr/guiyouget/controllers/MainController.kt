package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.events.RequestMediasWithYoutubeDL
import com.ingbyr.guiyouget.core.CoreContents
import com.ingbyr.guiyouget.core.YouGet
import com.ingbyr.guiyouget.core.YoutubeDL
import com.ingbyr.guiyouget.events.RequestMediasWithYouGet
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
            CoreContents.YOUTUBE_DL -> {
                fire(RequestMediasWithYoutubeDL(YoutubeDL(url)))
            }

            CoreContents.YOU_GET -> {
                fire(RequestMediasWithYouGet(YouGet(url)))
            }

            else -> {
                logger.error("bad downloading core $core")
            }
        }

    }

    fun updateCore() {
        // TODO: check updates
        // https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py
    }
}
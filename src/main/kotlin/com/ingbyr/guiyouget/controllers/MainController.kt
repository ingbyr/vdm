package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.engine.YouGet
import com.ingbyr.guiyouget.engine.YoutubeDL
import com.ingbyr.guiyouget.events.RequestMediasWithYouGet
import com.ingbyr.guiyouget.events.RequestMediasWithYoutubeDL
import com.ingbyr.guiyouget.utils.ContentsUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*


class MainController : Controller() {
    private val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    fun requestMediaInfo(url: String) {
        val core = app.config[ContentsUtil.DOWNLOAD_CORE]
        logger.debug("download engine is $core")

        // Init the request args
        when (core) {
            ContentsUtil.YOUTUBE_DL -> {
                fire(RequestMediasWithYoutubeDL(YoutubeDL(url)))
            }

            ContentsUtil.YOU_GET -> {
                fire(RequestMediasWithYouGet(YouGet(url)))
            }

            else -> {
                logger.error("bad downloading engine $core")
            }
        }

    }


    fun updateGUI() {
        hostServices.showDocument(ContentsUtil.APP_UPDATE_URL)
    }
}
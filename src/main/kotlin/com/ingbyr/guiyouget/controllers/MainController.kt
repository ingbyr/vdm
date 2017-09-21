package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.events.LoadMediaListRequest
import com.ingbyr.guiyouget.utils.CoreArgs
import com.ingbyr.guiyouget.utils.YouGet
import com.ingbyr.guiyouget.utils.YoutubeDL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*

class MainController : Controller() {
    private val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    fun requestMediaInfo(url: String) {
        val core = app.config["core"] as String
        logger.debug("download core is $core")

        // Init the request args
        if (core == YoutubeDL.NAME) {
            val args = CoreArgs(YoutubeDL.core)
            args.add("simulator", "-j")
            args.add("--proxy", "socks5://127.0.0.1:1080/")
            args.add("url", url)
            fire(LoadMediaListRequest(args.build()))
        }

        if (core == YouGet.NAME) {
            fire(LoadMediaListRequest(YouGet.requestJsonAargs(url).build()))
        }
    }

    fun updateCore() {
        // TODO: check updates
        // https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py
    }
}
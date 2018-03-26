package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.engine.YouGet
import com.ingbyr.guiyouget.engine.YoutubeDL
import org.slf4j.LoggerFactory
import tornadofx.*

class ProgressController : Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var youget: YouGet? = null
    private var youtubedl: YoutubeDL? = null
    private var formatID: String? = null

}

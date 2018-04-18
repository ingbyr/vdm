package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.engine.AbstractEngine
import com.ingbyr.guiyouget.engine.EngineFactory
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.utils.EngineType
import com.ingbyr.guiyouget.utils.ProxyType
import org.slf4j.LoggerFactory
import tornadofx.Controller
import java.util.*

class MediaListController : Controller() {

    private val logger = LoggerFactory.getLogger(MediaListController::class.java)
    var engine: AbstractEngine? = null

    init {
        messages = ResourceBundle.getBundle("i18n/MediaListView")
        subscribe<StopBackgroundTask> {
            engine?.stopTask()
        }
    }


    fun requestMedia(engineType: EngineType, url: String, proxyType: ProxyType, address: String, port: String): JsonObject? {
        engine = EngineFactory.create(engineType)
        if (engine != null) {
            engine!!.url(url).addProxy(proxyType, address, port)
            try {
                return engine!!.fetchMediaJson()
            } catch (e: Exception) {
                logger.error(e.toString())
            }
        } else {
            logger.error("bad engine: $engineType")
        }
        return null
    }
}
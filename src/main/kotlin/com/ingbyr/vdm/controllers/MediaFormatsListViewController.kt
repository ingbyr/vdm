package com.ingbyr.vdm.controllers

import com.beust.klaxon.JsonObject
import com.ingbyr.vdm.engine.AbstractEngine
import com.ingbyr.vdm.engine.EngineFactory
import com.ingbyr.vdm.events.StopBackgroundTask
import com.ingbyr.vdm.utils.EngineType
import com.ingbyr.vdm.utils.ProxyType
import org.slf4j.LoggerFactory
import tornadofx.Controller
import java.util.*

class MediaFormatsListViewController : Controller() {

    private val logger = LoggerFactory.getLogger(MediaFormatsListViewController::class.java)
    var engine: AbstractEngine? = null

    init {
        messages = ResourceBundle.getBundle("i18n/MediaFormatsListView")
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
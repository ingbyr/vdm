package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engine.AbstractEngine
import com.ingbyr.vdm.engine.EngineFactory
import com.ingbyr.vdm.engine.MediaFormat
import com.ingbyr.vdm.events.StopBackgroundTask
import com.ingbyr.vdm.utils.EngineType
import com.ingbyr.vdm.utils.ProxyType
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class MediaFormatsController : Controller() {

    private val logger = LoggerFactory.getLogger(MediaFormatsController::class.java)
    var engine: AbstractEngine? = null

    init {
        messages = ResourceBundle.getBundle("i18n/MediaFormatsView")
        subscribe<StopBackgroundTask> {
            engine?.stopTask()
        }
    }


    fun requestMedia(engineType: EngineType, url: String, proxyType: ProxyType, address: String, port: String): List<MediaFormat>? {
        engine = EngineFactory.create(engineType)
        if (engine != null) {
            engine!!.url(url).addProxy(proxyType, address, port)
            try {
                val jsonData = engine!!.fetchMediaJson()
                return engine!!.parseFormatsJson(jsonData)
            } catch (e: Exception) {
                logger.error(e.toString())
            }
        } else {
            logger.error("bad engine: $engineType")
        }
        return null
    }

    fun clear() {
        engine = null
    }
}
package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.AbstractEngine
import com.ingbyr.vdm.engines.utils.EngineFactory
import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.StopBackgroundTask
import com.ingbyr.vdm.models.MediaFormat
import com.ingbyr.vdm.utils.VDMProxy
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


    fun requestMedia(engineType: EngineType, url: String, proxy: VDMProxy): List<MediaFormat>? {
        engine = EngineFactory.create(engineType)
        if (engine != null) {
            engine!!.url(url).addProxy(proxy)
            try {
                val jsonData = engine!!.fetchMediaJson()
                return engine!!.parseFormatsJson(jsonData)
            } catch (e: Exception) {
                logger.error(e.toString())
            }
        } else {
            logger.error("bad engines: $engineType")
        }
        return null
    }

    fun clear() {
        engine = null
    }
}
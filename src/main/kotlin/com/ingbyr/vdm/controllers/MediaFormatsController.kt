package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.AbstractEngine
import com.ingbyr.vdm.engines.utils.EngineFactory
import com.ingbyr.vdm.events.StopBackgroundTask
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.MediaFormat
import com.ingbyr.vdm.utils.ConfigUtils
import com.ingbyr.vdm.utils.Attributes
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


    fun requestMedia(downloadTaskModel: DownloadTaskModel): List<MediaFormat>? {
        val config = downloadTaskModel.taskConfig
        val charset = ConfigUtils.safeLoad(Attributes.CHARSET, "UTF-8")
        engine = EngineFactory.create(config.engineType, charset)
        if (engine != null) {
            engine!!.simulateJson().addProxy(config.proxyType, config.proxyAddress, config.proxyPort).url(config.url)
            try {
                val jsonData = engine!!.fetchMediaJson()
                return engine!!.parseFormatsJson(jsonData)
            } catch (e: Exception) {
                logger.error(e.toString())
            }
        } else {
            logger.error("bad engine: ${downloadTaskModel.taskConfig.engineType}")
        }
        return null
    }

    fun clear() {
        engine = null
    }
}
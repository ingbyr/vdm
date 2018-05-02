package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.engine.AbstractEngine
import com.ingbyr.guiyouget.engine.EngineFactory
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.models.CurrentConfig
import org.slf4j.LoggerFactory
import tornadofx.Controller
import java.util.concurrent.ConcurrentLinkedQueue


class ProgressController : Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    var engine: AbstractEngine? = null

    init {
        subscribe<StopBackgroundTask> {
            engine?.apply {
                this.stopTask()
                logger.debug("stop the background task")
            }
        }
    }

    fun download(ccf: CurrentConfig, msgQueue: ConcurrentLinkedQueue<Map<String, Any>>) {
        engine = EngineFactory.create(ccf.engineType)
        if (engine != null) {
            engine!!.url(ccf.url).addProxy(ccf.proxyType, ccf.address, ccf.port).format(ccf.formatID).output(ccf.output).downloadMedia(msgQueue)
            engine = null
        } else {
            logger.error("bad engine ${ccf.engineType} when download media")
        }
    }
}

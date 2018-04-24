package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.engine.AbstractEngine
import com.ingbyr.guiyouget.engine.EngineFactory
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.utils.EngineType
import com.ingbyr.guiyouget.utils.ProxyType
import org.slf4j.LoggerFactory
import tornadofx.Controller
import java.util.concurrent.ConcurrentLinkedDeque

// todo import OSGi to add engine dynamically

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

    fun download(engineType: EngineType, url: String, proxyType: ProxyType, address: String, port: String,
                 formatID: String, output: String, msgQueue: ConcurrentLinkedDeque<Map<String, Any>>) {
        engine = EngineFactory.create(engineType)
        if (engine != null) {
            engine!!.url(url).addProxy(proxyType, address, port).format(formatID).output(output).downloadMedia(msgQueue)
            engine = null
        } else {
            logger.error("bad engine $engineType when download media")
        }
    }
}

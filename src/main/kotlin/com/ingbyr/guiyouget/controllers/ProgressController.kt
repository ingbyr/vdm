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
            logger.debug("stop the background task")
            engine?.stopTask()
        }
    }

    fun download(engineType: EngineType, url: String, proxyType: ProxyType, address: String, port: String, formatID: String, msgQueue: ConcurrentLinkedDeque<Map<String, Any>>) {
        engine = EngineFactory.create(engineType)
        if (engine != null) {
            engine!!.url(url).addProxy(proxyType, address, port).format(formatID).downloadMedia(msgQueue)
        }else {
            logger.error("bad engine $engineType when download media")
        }
    }
}

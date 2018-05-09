package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engine.AbstractEngine
import com.ingbyr.vdm.engine.EngineFactory
import com.ingbyr.vdm.events.StopBackgroundTask
import com.ingbyr.vdm.utils.VDMConfig
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

    fun download(ccf: VDMConfig, msgQueue: ConcurrentLinkedQueue<Map<String, Any>>) {
        engine = EngineFactory.create(ccf.engineType)
        if (engine != null) {
//            engine!!.url(ccf.url).addProxy(ccf.proxyType, ccf.address, ccf.port).format(ccf.formatID).output(ccf.output).downloadMedia(msgQueue)
//            engine = null
        } else {
            logger.error("bad engine ${ccf.engineType} when download media")
        }
    }
}

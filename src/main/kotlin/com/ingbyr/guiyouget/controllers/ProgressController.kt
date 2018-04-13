package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.engine.BaseEngine
import com.ingbyr.guiyouget.engine.YoutubeDL
import com.ingbyr.guiyouget.utils.CommonUtils
import com.ingbyr.guiyouget.utils.EngineUtils
import com.ingbyr.guiyouget.utils.ProxyUtils
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.concurrent.ConcurrentLinkedDeque

// todo import OSGi to add engine dynamically

class ProgressController : Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    lateinit var engine: BaseEngine

    fun download(url: String, formatID: String, msgQueue: ConcurrentLinkedDeque<Map<String, Any>>) {
        when (app.config[EngineUtils.TYPE]) {
            EngineUtils.YOUTUBE_DL -> {
                engine = YoutubeDL(url, msgQueue)
                engine.addProxy(
                        app.config.string(ProxyUtils.TYPE),
                        app.config.string(ProxyUtils.ADDRESS),
                        app.config.string(ProxyUtils.PORT))
                engine.downloadMedia(formatID, app.config.string(CommonUtils.STORAGE_PATH))
            }


            EngineUtils.YOU_GET -> {
                //todo you-get
            }
        }
    }
}

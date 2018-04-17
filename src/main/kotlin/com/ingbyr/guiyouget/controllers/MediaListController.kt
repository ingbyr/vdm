package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.engine.AbstractEngine
import com.ingbyr.guiyouget.engine.EngineFactory
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.models.Media
import com.ingbyr.guiyouget.utils.EngineType
import com.ingbyr.guiyouget.utils.ProxyType
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import org.slf4j.LoggerFactory
import tornadofx.Controller
import tornadofx.observable
import java.util.*

class MediaListController : Controller() {

    private val logger = LoggerFactory.getLogger(MediaListController::class.java)
    private var engine: AbstractEngine? = null

    init {
        messages = ResourceBundle.getBundle("i18n/MediaListView")

        subscribe<StopBackgroundTask> {
            engine?.stopTask()
        }
    }


    fun requestMedia(engineType: EngineType, url: String, proxyType: ProxyType, address: String, port: String): JsonObject? {
        // todo use engine type
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

    fun displayMedia(engineType: EngineType, labelTitle: Label, labelDescription: Label, listViewMedia: JFXListView<Label>, it: JsonObject) {
        when (engineType) {
            EngineType.YOUTUBE_DL -> {
                labelTitle.text = it.string("title")
                labelDescription.text = it.string("description") ?: ""
                addMediaItemsYoutubeDL(listViewMedia, it.array("formats"))
            }
            EngineType.YOU_GET -> {
                labelTitle.text = it.string("title")
                labelDescription.text = ""
                addMediaItemsYouGet(listViewMedia, it.array("streams"))
            }
            else -> {
                logger.error("bad engine $engineType when fetch media json")
            }
        }
    }

    // todo data display should be done in Engine class?
    private fun addMediaItemsYoutubeDL(listViewMedia: JFXListView<Label>, formats: JsonArray<JsonObject>?) {
        if (formats != null) {
            val medias = mutableListOf<Media>().observable()
            formats.mapTo(medias) {
                Media(it.string("format"),
                        it.string("format_note"),
                        it.int("filesize"),
                        it.string("format_id"),
                        it.string("ext"))
            }
            medias.reverse()
            medias.forEach {
                if (it.size == 0) {
                    listViewMedia.items.add(Label("${it.format} | ${it.ext}"))
                } else {
                    listViewMedia.items.add(Label("${it.format} | ${it.ext} | ${it.size}MB"))
                }
            }
        }
    }

    // todo data display should be done in Engine class?
    private fun addMediaItemsYouGet(listViewMedia: JFXListView<Label>, streams: JsonArray<JsonObject>?) {
        if (streams != null) {
            val streamsJson = streams as JsonObject
            val medias = mutableListOf<Media>().observable()
            logger.debug(streams.toString())
            streamsJson.forEach { t, _ ->
                val info = streamsJson[t] as JsonObject
                medias.add(Media(info.string("video_profile"),
                        "",
                        info.int("size"),
                        t,
                        info.string("container")))
            }
            medias.forEach {
                if (it.size == 0) {
                    listViewMedia.items.add(Label("${it.formatID} | ${it.format} | ${it.ext}"))
                } else {
                    listViewMedia.items.add(Label("${it.formatID} | ${it.format} | ${it.ext} | ${it.size}MB"))
                }
            }
        }
    }
}
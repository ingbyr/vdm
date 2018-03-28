package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.engine.BaseEngine
import com.ingbyr.guiyouget.engine.YoutubeDL
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.models.Media
import com.ingbyr.guiyouget.utils.EngineUtils
import com.ingbyr.guiyouget.utils.ProxyUtils
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class MediaListController : Controller() {

    private val logger = LoggerFactory.getLogger(MediaListController::class.java)
    var currentEngine: BaseEngine? = null

    init {
        messages = ResourceBundle.getBundle("i18n/MediaListView")

        subscribe<StopBackgroundTask> {
            currentEngine?.stop()
        }
    }

    fun requestMedia(url: String): JsonObject? {
        when (app.config[EngineUtils.TYPE]) {
            EngineUtils.YOUTUBE_DL -> {
                val engine = YoutubeDL(url)
                currentEngine = engine
                engine.addProxy(app.config.string(ProxyUtils.TYPE),
                        app.config.string(ProxyUtils.ADDRESS),
                        app.config.string(ProxyUtils.PORT))
                try {
                    return engine.fetchMediaJson()
                } catch (e: Exception) {
                    logger.error(e.toString())
                }
            }

            EngineUtils.YOU_GET -> {
            }
        }

        return null
    }

    fun displayMedia(labelTitle: Label, labelDescription: Label, listViewMedia: JFXListView<Label>, it: JsonObject) {
        when (app.config.string(EngineUtils.TYPE)) {
            EngineUtils.YOUTUBE_DL -> {
                labelTitle.text = it.string("title")
                labelDescription.text = it.string("description") ?: ""
                addMediaItemsYoutubeDL(listViewMedia, it.array("formats"))
            }
            EngineUtils.YOU_GET -> {
                labelTitle.text = it.string("title")
                labelDescription.text = ""
                addMediaItemsYouGet(listViewMedia, it.array("streams"))
            }
        }
    }

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
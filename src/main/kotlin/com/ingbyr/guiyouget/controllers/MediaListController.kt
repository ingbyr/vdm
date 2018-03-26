package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.engine.YouGet
import com.ingbyr.guiyouget.engine.YoutubeDL
import com.ingbyr.guiyouget.models.Media
import com.ingbyr.guiyouget.utils.ContentsUtil
import com.ingbyr.guiyouget.utils.EngineUtils
import com.ingbyr.guiyouget.utils.ProxyUtils
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class MediaListController : Controller() {

    private val logger = LoggerFactory.getLogger(MediaListController::class.java)

    init {
        messages = ResourceBundle.getBundle("i18n/MediaListView")
    }

    fun requestMedia(url: String): JsonObject {
        when (app.config[EngineUtils.DOWNLOAD_CORE]) {
            EngineUtils.YOUTUBE_DL -> {
                val engine = YoutubeDL(url)
                engine.addProxy(app.config.string(ProxyUtils.TYPE),
                        app.config.string(ProxyUtils.ADDRESS),
                        app.config.string(ProxyUtils.PORT))
                return engine.fetchMediaJson()
            }
            EngineUtils.YOU_GET -> {
                val engine = YouGet(url)
                engine.addProxy(app.config.string(ProxyUtils.TYPE),
                        app.config.string(ProxyUtils.ADDRESS),
                        app.config.string(ProxyUtils.PORT))
                return engine.fetchMediaJson()
            }
            else -> {
                // todo handle error
                return JsonObject()
            }
        }
    }

    fun addMediaItemsYoutubeDL(listViewMedia: JFXListView<Label>, formats: JsonArray<JsonObject>?) {
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

    fun addMediaItemsYouGet(listViewMedia: JFXListView<Label>, streams: JsonArray<JsonObject>?) {
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
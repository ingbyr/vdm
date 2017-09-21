package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.int
import com.beust.klaxon.string
import com.ingbyr.guiyouget.events.LoadMediaListRequest
import com.ingbyr.guiyouget.events.MediaListEvent
import com.ingbyr.guiyouget.models.Media
import com.ingbyr.guiyouget.utils.YouGet
import com.ingbyr.guiyouget.utils.YoutubeDL
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import org.slf4j.LoggerFactory
import tornadofx.*

class MediaListController : Controller() {
    private val logger = LoggerFactory.getLogger(MediaListController::class.java)
    fun subscribeEvents() {
        subscribe<LoadMediaListRequest> {
            try {
                val core = app.config["core"] as String
                when (core) {
                    YoutubeDL.NAME -> {
                        val json = YoutubeDL.getMediaInfo(it.args)
                        fire(MediaListEvent(json))
                    }
                    YouGet.NAME -> {
                        val json = YouGet.getMediaInfo(it.args)
                        fire(MediaListEvent(json))
                    }
                    else -> {
                        logger.error("Bad downloading core $core")
                    }
                }
            } catch (e: Exception) {
                logger.error(e.toString())
                fire(MediaListEvent(JsonObject(mapOf(
                        "title" to "Failed to get media info",
                        "description" to "Make sure that URL is correct"))))
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

            medias.forEach {
                if (it.size == 0) {
                    listViewMedia.items.add(Label("${it.format} | ${it.ext}"))
                } else {
                    listViewMedia.items.add(Label("${it.format} | ${it.ext} | ${it.size}MB"))
                }
            }
        }
    }

    fun addMediaItemsYouGet(listViewMedia: JFXListView<Label>, streams: JsonObject) {
        val medias = mutableListOf<Media>().observable()
        logger.debug(streams.toString())
        streams.forEach { t, u ->
            val info = streams.get(t) as JsonObject
            medias.add(Media(info.string("video_profile"),
                    "",
                    info.int("size"),
                    t,
                    info.string("container")))
        }

        medias.forEach {
            if (it.size == 0) {
                listViewMedia.items.add(Label("${it.format} | ${it.ext}"))
            } else {
                listViewMedia.items.add(Label("${it.format} | ${it.ext} | ${it.size}MB"))
            }
        }

    }
}
package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.int
import com.beust.klaxon.string
import com.ingbyr.guiyouget.events.DisplayMediasWithYouGet
import com.ingbyr.guiyouget.events.DisplayMediasWithYoutubeDL
import com.ingbyr.guiyouget.events.RequestMediasWithYouGet
import com.ingbyr.guiyouget.events.RequestMediasWithYoutubeDL
import com.ingbyr.guiyouget.models.Media
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

    fun subscribeEvents() {
        subscribe<RequestMediasWithYoutubeDL> {
            try {
                val json = it.youtubedl.getMediasInfo()
                fire(DisplayMediasWithYoutubeDL(json))
            } catch (e: Exception) {
                logger.error(e.toString())
                fire(DisplayMediasWithYoutubeDL(JsonObject(mapOf(
                        "title" to messages["failed"]))))
            }
        }

        subscribe<RequestMediasWithYouGet> {
            try {
                val json = it.youget.getMediasInfo()
                fire(DisplayMediasWithYouGet(json))
            } catch (e: Exception) {
                logger.error(e.toString())
                fire(DisplayMediasWithYouGet(JsonObject(mapOf("title" to messages["failed"]))))
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

    fun addMediaItemsYouGet(listViewMedia: JFXListView<Label>, streams: Any?) {
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
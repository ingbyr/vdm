package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.int
import com.beust.klaxon.string
import com.ingbyr.guiyouget.events.DownloadMediaRequest
import com.ingbyr.guiyouget.events.LoadMediaListRequest
import com.ingbyr.guiyouget.events.MediaListEvent
import com.ingbyr.guiyouget.models.Media
import com.ingbyr.guiyouget.utils.CoreArgs
import com.ingbyr.guiyouget.utils.YoutubeDL
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import tornadofx.*

class MediaListController : Controller() {

    private val core = config.string("core", YoutubeDL.NAME)

    fun subscribeEvents() {
        subscribe<LoadMediaListRequest> {
            try {
                val json = YoutubeDL.getMediaInfo(it.args)
                fire(MediaListEvent(json))
            } catch (e: Exception) {
                log.warning(e.toString())
                fire(MediaListEvent(JsonObject(mapOf(
                        "title" to "Failed to get media info",
                        "description" to "Make sure that URL is correct"))))
            }
        }

        subscribe<DownloadMediaRequest> {
            if (core == YoutubeDL.NAME) {
                try {
                    val args = CoreArgs(YoutubeDL.core)
                    args.add("-f", it.formatID)
                    args.add("--proxy", "socks5://127.0.0.1:1080/")
                    args.add("url", it.url)
                    YoutubeDL.downloadMedia(this@MediaListController, args.build())
                } catch (e: Exception) {
                    log.warning(e.toString())
                }
            }

        }
    }

    fun addMediaItems(listViewMedia: JFXListView<Label>, formats: JsonArray<JsonObject>?) {
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

    fun updateProgress(progress: Double) {
        println(progress)
    }
}
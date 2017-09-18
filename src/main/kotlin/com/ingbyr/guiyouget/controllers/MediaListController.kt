package com.ingbyr.guiyouget.controllers

import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.events.LoadMediaListRequest
import com.ingbyr.guiyouget.events.MediaListEvent
import com.ingbyr.guiyouget.utils.YoutubeDLUtils
import tornadofx.*

class MediaListController : Controller() {

    init {
        subscribe<LoadMediaListRequest> {
            try {
                val json = YoutubeDLUtils.getMediaInfo(it.args)
                fire(MediaListEvent(json))
            } catch (e: Exception) {
                fire(MediaListEvent(JsonObject(mapOf(
                        "title" to "Failed to get media info",
                        "description" to "Make sure that URL is correct"))))
            }
        }
    }

}
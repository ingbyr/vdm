package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.events.LoadMediaListRequest
import com.ingbyr.guiyouget.events.MediaListEvent
import com.ingbyr.guiyouget.utils.YoutubeDLUtils
import tornadofx.*

class MediaListController : Controller() {

    init {
        subscribe<LoadMediaListRequest> {
            val json = YoutubeDLUtils.getMediaInfo(it.args)
            fire(MediaListEvent(json))
        }
    }

}
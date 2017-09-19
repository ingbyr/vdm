package com.ingbyr.guiyouget.events

import com.beust.klaxon.JsonObject
import tornadofx.*

class MediaListEvent(val mediaList: JsonObject) : FXEvent()

class LoadMediaListRequest(val args: MutableList<String>) : FXEvent(EventBus.RunOn.BackgroundThread)

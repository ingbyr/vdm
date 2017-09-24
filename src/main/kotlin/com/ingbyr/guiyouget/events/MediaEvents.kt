package com.ingbyr.guiyouget.events

import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.core.YouGet
import com.ingbyr.guiyouget.core.YoutubeDL
import tornadofx.*

// common events
object StopDownloading : FXEvent()

// youtube-dl events
class DisplayMediasWithYoutubeDL(val mediaList: JsonObject) : FXEvent()

class RequestMediasWithYoutubeDL(val youtubedl: YoutubeDL) : FXEvent(EventBus.RunOn.BackgroundThread)

class DownloadingRequestWithYoutubeDL(val url: String, val formatID: String) : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateProgressWithYoutubeDL(val progress: Double, val speed: String, val extime: String, val status: String) : FXEvent()

// you-get events
class DisplayMediasWithYouGet(val mediaList: JsonObject) : FXEvent()

class RequestMediasWithYouGet(val youget: YouGet) : FXEvent(EventBus.RunOn.BackgroundThread)

class DownloadingRequestWithYouGet(val url: String, val formatID: String) : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateProgressWithYouGet(val progress: Double, val speed: String, val status: String) : FXEvent()

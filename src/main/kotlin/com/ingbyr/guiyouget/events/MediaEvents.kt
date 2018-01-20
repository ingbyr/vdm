package com.ingbyr.guiyouget.events

import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.engine.YouGet
import com.ingbyr.guiyouget.engine.YoutubeDL
import tornadofx.*

// common events
object StopDownloading : FXEvent()

object ResumeDownloading : FXEvent(EventBus.RunOn.BackgroundThread)

// youtube-dl events
class DisplayMediasWithYoutubeDL(val mediaList: JsonObject) : FXEvent()

class RequestMediasWithYoutubeDL(val youtubedl: YoutubeDL) : FXEvent(EventBus.RunOn.BackgroundThread)

class DownloadingRequestWithYoutubeDL(val youtubedl: YoutubeDL, val formatID: String) : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateProgressWithYoutubeDL(val progress: Double, val speed: String, val extime: String, val status: String) : FXEvent()

// you-get events
class DisplayMediasWithYouGet(val mediaList: JsonObject) : FXEvent()

class RequestMediasWithYouGet(val youget: YouGet) : FXEvent(EventBus.RunOn.BackgroundThread)

class DownloadingRequestWithYouGet(val youget: YouGet, val formatID: String) : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateProgressWithYouGet(val progress: Double, val speed: String, val status: String) : FXEvent()

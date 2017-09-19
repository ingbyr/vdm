package com.ingbyr.guiyouget.events

import tornadofx.*

class DownloadMediaRequest(val url: String, val formatID: String) : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateMediaProgressbar(val progress: Double, val speed: String, val extTime: String) : FXEvent()

object StopDownload : FXEvent()
package com.ingbyr.guiyouget.events

import com.ingbyr.guiyouget.models.CurrentConfig
import tornadofx.FXEvent

object StopBackgroundTask : FXEvent()

class DownloadMedia(val ccf: CurrentConfig) : FXEvent()
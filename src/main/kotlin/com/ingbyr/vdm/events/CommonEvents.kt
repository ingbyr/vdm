package com.ingbyr.vdm.events

import com.ingbyr.vdm.utils.VDMConfig
import tornadofx.FXEvent

object StopBackgroundTask : FXEvent()

class DownloadMedia(val ccf: VDMConfig) : FXEvent()
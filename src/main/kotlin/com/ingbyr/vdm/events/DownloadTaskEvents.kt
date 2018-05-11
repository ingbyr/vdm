package com.ingbyr.vdm.events

import com.ingbyr.vdm.models.DownloadTask
import tornadofx.*

object StopBackgroundTask : FXEvent()

class CreateDownloadTask(val downloadTask: DownloadTask) : FXEvent()
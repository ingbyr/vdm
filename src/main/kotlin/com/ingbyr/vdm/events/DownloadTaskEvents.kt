package com.ingbyr.vdm.events

import com.ingbyr.vdm.models.DownloadTaskData
import tornadofx.*

object StopBackgroundTask : FXEvent()

class CreateDownloadTask(val downloadTask: DownloadTaskData) : FXEvent()
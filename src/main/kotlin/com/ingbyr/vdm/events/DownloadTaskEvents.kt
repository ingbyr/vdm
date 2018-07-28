package com.ingbyr.vdm.events

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskModel
import tornadofx.*

class StopBackgroundTask(val downloadTask: DownloadTaskModel? = null, val stopAll: Boolean = false) : FXEvent()

class CreateDownloadTask(val downloadTask: DownloadTaskModel) : FXEvent()

class UpdateEngineTask(val engineType: EngineType, val localVersion: String) : FXEvent(EventBus.RunOn.BackgroundThread)
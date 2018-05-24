package com.ingbyr.vdm.events

import com.ingbyr.vdm.engine.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskData
import com.ingbyr.vdm.models.DownloadTaskModel
import tornadofx.*

class StopBackgroundTask(val downloadTask: DownloadTaskModel? = null, val stopAll: Boolean = false) : FXEvent()

class CreateDownloadTask(val downloadTask: DownloadTaskData) : FXEvent()

class UpdateEngineTask(val engineType: EngineType, val localVersion: String) : FXEvent(EventBus.RunOn.BackgroundThread)
package com.ingbyr.guiyouget.events

import tornadofx.*

// common events
object StopBackgroundTask : FXEvent()

object ResumeDownloading : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateProgressView()

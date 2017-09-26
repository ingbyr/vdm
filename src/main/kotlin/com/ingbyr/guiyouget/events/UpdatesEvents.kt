package com.ingbyr.guiyouget.events

import tornadofx.*

// you-get updates
object RequestCheckUpdatesYouGet : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateYouGetStates(val status: String) : FXEvent()

// you-get updates
object RequestCheckUpdatesYoutubeDL : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateYoutubeDLStates(val status: String) : FXEvent()
package com.ingbyr.vdm.events

import tornadofx.EventBus
import tornadofx.FXEvent

// you-get updates
object RequestCheckUpdatesYouGet : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateYouGetStates(val status: String) : FXEvent()

// youtube-dl updates
object RequestCheckUpdatesYoutubeDL : FXEvent(EventBus.RunOn.BackgroundThread)

class UpdateYoutubeDLStates(val status: String) : FXEvent()
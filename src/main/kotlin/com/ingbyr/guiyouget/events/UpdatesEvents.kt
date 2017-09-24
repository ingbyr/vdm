package com.ingbyr.guiyouget.events

import tornadofx.*

// common event
class UpdateStates(val status: String) : FXEvent()

// you-get updates
object RequestCheckUpdatesYouGet : FXEvent(EventBus.RunOn.BackgroundThread)

object UpdateYouGet : FXEvent(EventBus.RunOn.BackgroundThread)

// you-get updates
object RequestCheckUpdatesYoutubeDL : FXEvent(EventBus.RunOn.BackgroundThread)

object UpdateYoutubeDL : FXEvent(EventBus.RunOn.BackgroundThread)


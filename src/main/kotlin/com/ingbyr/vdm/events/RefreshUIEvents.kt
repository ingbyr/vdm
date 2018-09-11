package com.ingbyr.vdm.events

import com.ingbyr.vdm.engines.utils.EngineType
import tornadofx.*

class RefreshEngineVersion(val engineType: EngineType, val newVersion: String) : FXEvent()

object RefreshCookieContent : FXEvent()
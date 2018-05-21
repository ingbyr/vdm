package com.ingbyr.vdm.events

import com.ingbyr.vdm.utils.EngineType
import tornadofx.*

class RefreshEngineVersion(val engineType: EngineType, val newVersion: String) : FXEvent()
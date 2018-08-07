package com.ingbyr.vdm.models

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.utils.VDMProxy

data class TaskEngineConfig(
        val engineType: EngineType,
        val proxy: VDMProxy,
        val downloadDefaultFormat: Boolean,
        val storagePath: String,
        val cookie: String = "",
        val ffmpeg: String = "")
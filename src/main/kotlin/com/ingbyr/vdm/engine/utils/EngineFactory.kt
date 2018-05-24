package com.ingbyr.vdm.engine.utils

import com.ingbyr.vdm.engine.AbstractEngine
import com.ingbyr.vdm.engine.YoutubeDL

object EngineFactory {
    fun create(engineType: EngineType): AbstractEngine? {
        return when (engineType) {
            EngineType.YOUTUBE_DL -> YoutubeDL()
            else -> null
        }
    }
}

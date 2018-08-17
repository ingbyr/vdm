package com.ingbyr.vdm.engines.utils

import com.ingbyr.vdm.engines.AbstractEngine
import com.ingbyr.vdm.engines.YoutubeDL

object EngineFactory {
    fun create(engineType: EngineType): AbstractEngine {
        return when (engineType) {
            EngineType.YOUTUBE_DL -> YoutubeDL()
            else -> throw EngineException("bad engine type: $engineType")
        }
    }
}

package com.ingbyr.vdm.engines.utils

import com.ingbyr.vdm.engines.AbstractEngine
import com.ingbyr.vdm.engines.YoutubeDL

object EngineFactory {
    fun create(engineType: EngineType, charset: String): AbstractEngine {
        return when (engineType) {
            EngineType.YOUTUBE_DL -> {
                val engine = YoutubeDL()
                engine.charset = charset
                engine
            }
            else -> throw EngineException("bad engine type: $engineType")
        }
    }
}

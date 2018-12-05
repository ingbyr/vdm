package com.ingbyr.vdm.engines.utils

import com.ingbyr.vdm.engines.AbstractEngine
import com.ingbyr.vdm.engines.Annie
import com.ingbyr.vdm.engines.YoutubeDL

object EngineFactory {
    fun create(engineType: EngineType, charset: String = "UTF-8"): AbstractEngine {
        return when (engineType) {
            EngineType.YOUTUBE_DL -> {
                val engine = YoutubeDL()
                engine.charset = charset
                engine
            }
            EngineType.ANNIE -> {
                val engine = Annie()
                engine.charset = charset
                engine
            }
        }
    }
}

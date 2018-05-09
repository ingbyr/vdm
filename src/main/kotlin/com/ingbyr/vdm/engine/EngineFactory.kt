package com.ingbyr.vdm.engine

import com.ingbyr.vdm.utils.EngineType

object EngineFactory {
    fun create(engineType: EngineType): AbstractEngine? {
        return when (engineType) {
            EngineType.YOUTUBE_DL -> YoutubeDL()
            else -> null
        }
    }
}


//fun main(args: Array<String>) {
//    val engine = EngineFactory.create(EngineType.YOUTUBE_DL)
//    if (engine != null) {
//        engine.url("https://test-url.com")
//        engine.addProxy(ProxyType.HTTP, "127.0.0.1", "1080")
//        engine.displayCommand()
//    } else {
//        println("oop")
//    }
//}
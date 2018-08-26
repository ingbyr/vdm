package com.ingbyr.vdm.engines

import com.ingbyr.vdm.engines.utils.EngineFactory
import com.ingbyr.vdm.engines.utils.EngineType
import org.junit.jupiter.api.Test

class AnnieTests {

    @Test
    fun `parse the media json`() {
        val engine = EngineFactory.create(EngineType.ANNIE, "UTF-8")
        engine.simulateJson().url("https://www.bilibili.com/video/av30092341/").fetchMediaJson()
    }

}
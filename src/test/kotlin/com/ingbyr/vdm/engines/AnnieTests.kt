package com.ingbyr.vdm.engines

import com.ingbyr.vdm.engines.utils.EngineFactory
import com.ingbyr.vdm.engines.utils.EngineType
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class AnnieTests {

    @Test
    fun `parse the media json`() {
        val engine = EngineFactory.create(EngineType.ANNIE, "UTF-8")
        engine.simulateJson().url("https://www.bilibili.com/video/av30092341/").fetchMediaJson()
    }

    @Test
    fun `parse download output`() {
        val line = "5.46 MiB / 17.22 MiB   31.73% 645.46 KiB/s 00m18s"
        val progressPattern = Pattern.compile("\\d+\\.\\d*%")
        val speedPattern = Pattern.compile("\\d+\\.\\d*\\s+\\w+/s")

        val process = progressPattern.matcher(line).takeIf { it.find() }?.group()
        val speed = speedPattern.matcher(line).takeIf { it.find() }?.group()
        println("process $process, speed $speed")
    }
}
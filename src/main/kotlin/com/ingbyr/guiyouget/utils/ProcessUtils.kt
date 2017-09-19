package com.ingbyr.guiyouget.utils

import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.events.UpdateMediaProgressbar
import java.io.BufferedReader
import java.io.InputStreamReader


object ProcessUtils {
    private var progress: Double? = null
    private var speed: String? = null
    private var extTime: String? = null

    fun runCommand(args: MutableList<String>): StringBuilder {
        val output = StringBuilder()
        val builder = ProcessBuilder(args)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?
        while (true) {
            line = r.readLine()
            if (line == null) {
                break
            }
            output.append(line.trim())
        }
        return output
    }

    //todo 解析下载进度和下载速度和剩余时间
    fun runDownloadCommand(controller: MediaListController, args: MutableList<String>) {
        val builder = ProcessBuilder(args)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?
//        var progress: Double? = null
//        var speed: String? = null
//        var extTime: String? = null
        while (true) {
            line = r.readLine()
            if (line == null) {
                break
            }
//            println(line)
            /**
             * [download]  95.9% of ~5.82MiB at 126.43KiB/s ETA 00:00
             * [download]  61.9% of 6.46MiB at  1.53MiB/s ETA 00:01
            [download] 100.0% of 6.46MiB at  1.95MiB/s ETA 00:00
            [download] 100% of 6.46MiB in 00:03
             */
            val outs = line.split(" ")
            outs.forEach {
                if (it.endsWith("%")) progress = it.subSequence(0, it.length - 1).toString().toDouble()
                if (it.endsWith("/s")) speed = it
                if (it.matches(Regex("\\d+:\\d+"))) extTime = it
            }
            if (progress != null && speed != null && extTime != null) {
//                println("$progress, $speed, $extTime")
                controller.fire(UpdateMediaProgressbar(progress!!, speed!!, extTime!!))
                progress = null
                speed = null
                extTime = null
            }
        }
    }
}
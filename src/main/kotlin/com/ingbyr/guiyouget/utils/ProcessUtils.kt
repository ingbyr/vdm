package com.ingbyr.guiyouget.utils

import com.ingbyr.guiyouget.models.Progress
import javafx.application.Platform
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader


object ProcessUtils {
    val logger = LoggerFactory.getLogger(ProcessUtils::class.java)
    var progress: Double? = null
    var speed: String? = null
    var extTime: String? = null

    fun runCommand(args: MutableList<String>): StringBuilder {
        val output = StringBuilder()
        val builder = ProcessBuilder(args)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream, charset("GBK")))
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

    //todo 暂停下载
    fun runDownloadCommand(pg: Progress, args: MutableList<String>) {
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
            logger.debug(line)
            parseYoutubeDLOutput(line, pg)
            if (progress == 100.0) {
                Platform.runLater {
                    pg.status = "Finished"
                }
            }
        }
    }

    fun parseYoutubeDLOutput(line: String, pg: Progress) {
        val outs = line.split(" ")
        outs.forEach {
            if (it.endsWith("%")) progress = it.subSequence(0, it.length - 1).toString().toDouble()
            if (it.endsWith("/s")) speed = it
            if (it.matches(Regex("\\d+:\\d+"))) extTime = it
        }

        if (progress != null) {
            //todo need a better way to update progress UI
            Platform.runLater {
                logger.debug("$progress, $speed, $extTime")
                pg.progress = progress as Double / 100
                pg.speed = speed
                pg.extTime = extTime
                pg.status = "Downloading..."
            }
        }

    }
}
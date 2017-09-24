package com.ingbyr.guiyouget.core

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYoutubeDL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths


class YoutubeDL(private val url: String) : CoreController() {

    private val core = this::class.java.getResource("/core/youtube-dl.exe").path
    private val parser = Parser()
    private var progress = 0.0
    private var speed = "0MiB/s"
    private var extTime = "00:00"
    private var status = "Analyzing..."
    private var isDownloading = false

    init {
        subscribe<StopDownloading> {
            isDownloading = false
        }
    }

    private fun requestJsonAargs(): CoreArgs {
        val args = CoreArgs(core)
        args.add("simulator", "-j")
        args.add("--proxy", "socks5://127.0.0.1:1080/")
        args.add("url", url)
        return args
    }

    fun getMediasInfo(): JsonObject {
        val output = runCommand(requestJsonAargs().build())
//        Files.write(Paths.get(System.getProperty("user.dir"), "info.json"), output.toString().toByteArray())
        return parser.parse(output) as JsonObject
    }


    override fun runDownloadCommand(formatID: String) {
        isDownloading = true
        var line: String?
        val args = CoreArgs(core)
        args.add("--proxy", "socks5://127.0.0.1:1080/")
        args.add("-f", formatID)
        args.add("url", url)
        val builder = ProcessBuilder(args.build())
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        while (true) {
            line = r.readLine()
            if (line != null && isDownloading) {
                logger.trace(line)
                logger.trace("downloading is $isDownloading")
                parseStatus(line)
            } else {
                if (p != null && p.isAlive) {
                    logger.debug("stop process $p")
                    p.destroy()
                }
                break
            }
        }
    }

    override fun parseStatus(line: String) {
        val outs = line.split(" ")

        outs.forEach {
            if (it.endsWith("%")) progress = it.subSequence(0, it.length - 1).toString().toDouble()
            if (it.endsWith("/s")) speed = it
            if (it.matches(Regex("\\d+:\\d+"))) extTime = it
        }
        logger.trace("$progress, $speed, $extTime, $status")
        if (progress == 100.0) {
            status = "Completed"
        }
        fire(UpdateProgressWithYoutubeDL(progress, speed, extTime, status))
    }
}
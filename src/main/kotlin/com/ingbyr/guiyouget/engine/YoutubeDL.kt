package com.ingbyr.guiyouget.engine

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYoutubeDL
import com.ingbyr.guiyouget.utils.ContentsUtil
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths


class YoutubeDL(private val url: String) : DownloadEngineController() {
    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }

    val core = Paths.get(System.getProperty("user.dir"), "engine", "youtube-dl.exe").toAbsolutePath().toString()
    private val parser = Parser()
    private var progress = 0.0
    private var speed = "0MiB/s"
    private var extTime = "00:00"
    private var status = messages["analyzing"]
    private var isDownloading = false
    private val outputTemplate = "%(title)s.%(ext)s"

    init {
        subscribe<StopDownloading> {
            isDownloading = false
        }
    }

    private fun requestJsonArgs(): DownloadEngine {
        val engine = DownloadEngine(core)
        engine.add("simulator", "-j")
        when (app.config[ContentsUtil.PROXY_TYPE]) {
            ContentsUtil.PROXY_SOCKS -> {
                engine.add("--proxy",
                        "socks5://${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}/")
            }
            ContentsUtil.PROXY_HTTP -> {
                engine.add("--proxy",
                        "${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}")
            }
        }
        engine.add("url", "\"$url\"")
        return engine
    }

    fun getMediasInfo(): JsonObject {
        val output = runCommand(requestJsonArgs().build())
//        Files.write(Paths.get(System.getProperty("user.dir"), "info.json"), output.toString().toByteArray())
        return parser.parse(output) as JsonObject
    }


    override fun runDownloadCommand(formatID: String) {
        isDownloading = true
        status = messages["downloading"]
        var line: String?
        val engine = DownloadEngine(core)
        when (app.config[ContentsUtil.PROXY_TYPE]) {
            ContentsUtil.PROXY_SOCKS -> {
                engine.add("--proxy",
                        "socks5://${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}/")
            }
            ContentsUtil.PROXY_HTTP -> {
                engine.add("--proxy",
                        "${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}")
            }
        }
        engine.add("-f", formatID)
        engine.add("-o", Paths.get(app.config[ContentsUtil.STORAGE_PATH] as String, outputTemplate).toString())
        engine.add("url", "\"$url\"")
        val builder = ProcessBuilder(engine.build())
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
                    p.destroyForcibly()
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
            status = messages["completed"]
        }
        fire(UpdateProgressWithYoutubeDL(progress, speed, extTime, status))
    }
}
package com.ingbyr.guiyouget.engine

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYouGet
import com.ingbyr.guiyouget.utils.ContentsUtil
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

class YouGet(val url: String) : DownloadEngineController() {
    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }

    val core = Paths.get(System.getProperty("user.dir"), "engine", "you-get.exe").toAbsolutePath().toString()
    private val parser = Parser()
    private var progress = 0.0
    private var speed = "0MB/s"
    private var status = messages["analyzing"]
    private var isDownloading = false

    init {
        subscribe<StopDownloading> {
            isDownloading = false
        }
    }

    private fun requestJsonAargs(): DownloadEngine {
        val engine = DownloadEngine(core)
        engine.add("simulator", "--json")
        when (app.config[ContentsUtil.PROXY_TYPE]) {
            ContentsUtil.PROXY_SOCKS -> {
                engine.add("-x",
                        "${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}")
            }
            ContentsUtil.PROXY_HTTP -> {
                engine.add("-x",
                        "${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}")
            }
        }
        engine.add("url", "\"$url\"")

        return engine
    }

    fun getMediasInfo(): JsonObject {
        val output = runCommand(requestJsonAargs().build())
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
                engine.add("-x",
                        "${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}")
            }
            ContentsUtil.PROXY_HTTP -> {
                engine.add("-x",
                        "${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}")
            }
        }
        engine.add("foramtID", "--itag=$formatID")
        engine.add("-o", app.config[ContentsUtil.STORAGE_PATH] as String)
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
                    p.destroy()
                }
                break
            }
        }
    }

    override fun parseStatus(line: String) {
        val p = Regex("\\d+\\.*\\d*%").findAll(line).toList().flatMap(MatchResult::groupValues)
        if (p.isNotEmpty()) {
            progress = p[0].subSequence(0, p[0].length - 1).toString().toDouble()
        }

        val s = Regex("\\d+\\s*.B/s").findAll(line).toList().flatMap(MatchResult::groupValues)
        if (s.isNotEmpty()) {
            speed = s[0]
        }

        logger.trace("$progress, $speed, $status")
        if (progress == 100.0) {
            status = messages["completed"]
        }
        fire(UpdateProgressWithYouGet(progress, speed, status))
    }
}
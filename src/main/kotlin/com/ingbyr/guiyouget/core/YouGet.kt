package com.ingbyr.guiyouget.core

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYouGet
import com.ingbyr.guiyouget.utils.CoreUtils
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

class YouGet(val url: String) : CoreController() {
    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }

    val core = Paths.get(System.getProperty("user.dir"), "core", "you-get.exe").toAbsolutePath().toString()
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

    private fun requestJsonAargs(): CoreArgs {
        val args = CoreArgs(core)
        args.add("simulator", "--json")
        when (app.config[CoreUtils.PROXY_TYPE]) {
            CoreUtils.PROXY_SOCKS -> {
                args.add("-x",
                        "${app.config[CoreUtils.PROXY_ADDRESS]}:${app.config[CoreUtils.PROXY_PORT]}")
            }
            CoreUtils.PROXY_HTTP -> {
                args.add("-x",
                        "${app.config[CoreUtils.PROXY_ADDRESS]}:${app.config[CoreUtils.PROXY_PORT]}")
            }
        }
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
        status = messages["downloading"]
        var line: String?
        val args = CoreArgs(core)
        when (app.config[CoreUtils.PROXY_TYPE]) {
            CoreUtils.PROXY_SOCKS -> {
                args.add("-x",
                        "${app.config[CoreUtils.PROXY_ADDRESS]}:${app.config[CoreUtils.PROXY_PORT]}")
            }
            CoreUtils.PROXY_HTTP -> {
                args.add("-x",
                        "${app.config[CoreUtils.PROXY_ADDRESS]}:${app.config[CoreUtils.PROXY_PORT]}")
            }
        }
        args.add("foramtID", "--itag=$formatID")
        args.add("-o", app.config["storagePath"] as String)
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
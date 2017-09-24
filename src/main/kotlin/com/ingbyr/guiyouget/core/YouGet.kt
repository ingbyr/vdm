package com.ingbyr.guiyouget.core

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.events.UpdateProgressWithYouGet
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

class YouGet(val url: String) : CoreController() {
    val core = this::class.java.getResource("/core/you-get.exe").path
    val parser = Parser()
    private var progress = 0.0
    private var speed = "0MB/s"
    private var status = "Analyzing..."
    private var isDownloading = false

    private fun requestJsonAargs(): CoreArgs {
        val args = CoreArgs(core)
        args.add("simulator", "--json")
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
        args.add("foramtID", "--itag=$formatID")
        args.add("url", url)
        val builder = ProcessBuilder(args.build())
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        while (true) {
            line = r.readLine()
            if (line != null && isDownloading) {
                logger.debug(line)
                logger.debug("downloading is $isDownloading")
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
        val p = Regex("\\s*\\d+%").findAll(line).toList().flatMap(MatchResult::groupValues)
        if (p.isNotEmpty()) {
            progress = p[0].subSequence(0, p[0].length - 1).toString().toDouble()
        }

        val s = Regex("\\d+\\s*.B/s").findAll(line).toList().flatMap(MatchResult::groupValues)
        if (s.isNotEmpty()) {
            speed = s[0]
        }

        logger.debug("$progress, $speed, $status")
        if (progress == 100.0) {
            status = "Completed"
        }
        fire(UpdateProgressWithYouGet(progress, speed, status))
    }
}
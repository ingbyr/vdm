package com.ingbyr.guiyouget.engine

import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYouGet
import com.ingbyr.guiyouget.utils.ContentsUtil
import com.ingbyr.guiyouget.utils.OSException
import com.ingbyr.guiyouget.utils.Platform
import com.ingbyr.guiyouget.utils.PlatformType
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

class YouGet(val url: String) : DownloadEngineController() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val core = initCore()
    private var progress = 0.0
    private var speed = "0MB/s"
    private var status = messages["analyzing"]
    private var isRunning = false


    init {
        subscribe<StopDownloading> {
            isRunning = false
        }
    }

    private fun initCore(): String {
        /**
         * init the core path for different platforms
         */
        return when (Platform.current()) {
            PlatformType.WINDOWS -> {
                Paths.get(System.getProperty("user.dir"), "engine", "you-get.exe").toAbsolutePath().toString()
            }
            PlatformType.LINUX -> {
                "you-get"
            }
            PlatformType.MAC_OS -> {
                "you-get"
            }
            PlatformType.NOT_SUPPORTED -> {
                logger.error("Not supported OS")
                throw OSException("Not supported OS")
            }
        }
    }

    private fun addProxyArg(engine: DownloadEngineArgsBuilder) {
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
    }

    private fun requestJsonArgs(): DownloadEngineArgsBuilder {
        /**
         *  Build args for requesting the json data
         */
        val engineBuilder = DownloadEngineArgsBuilder(core)
        engineBuilder.add("simulator", "--json")
        addProxyArg(engineBuilder)
        engineBuilder.add("url", url)
        return engineBuilder
    }

    fun getMediasInfo(): StringBuilder {
        /**
         *  Get json data of the media
         */
        val output = request(requestJsonArgs().build())
//        Files.write(Paths.get(System.getProperty("user.dir"), "info.json"), output.toString().toByteArray())
        return output
    }

    override fun request(args: MutableList<String>): StringBuilder {
        isRunning = true
        val output = StringBuilder()
        val builder = ProcessBuilder(args)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?
        while (true) {
            line = r.readLine()
            if (!isRunning || line == null) {
                break
            }
            output.append(line.trim())
        }
        return output
    }

    override fun download(formatID: String) {
        isRunning = true
        status = messages["downloading"]
        var line: String?
        val engineBuilder = DownloadEngineArgsBuilder(core)
        addProxyArg(engineBuilder)
        engineBuilder.add("foramtID", "--itag=$formatID")
        engineBuilder.add("-o", app.config[ContentsUtil.STORAGE_PATH] as String)
        engineBuilder.add("url", url)
        val builder = ProcessBuilder(engineBuilder.build())
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        while (true) {
            line = r.readLine()
            if (line != null && isRunning) {
                logger.trace(line)
                logger.trace("downloading is $isRunning")
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
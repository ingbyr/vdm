package com.ingbyr.guiyouget.engine

import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYoutubeDL
import com.ingbyr.guiyouget.utils.ContentsUtil
import com.ingbyr.guiyouget.utils.OSException
import com.ingbyr.guiyouget.utils.Platform
import com.ingbyr.guiyouget.utils.PlatformType
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths


class YoutubeDL(private val url: String) : DownloadEngineController() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val core = initCore()
    private var progress = 0.0
    private var speed = "0MiB/s"
    private var extTime = "00:00"
    private var status = messages["analyzing"]
    private var isRunning = false
    private val outputTemplate = "%(title)s.%(ext)s"

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
                Paths.get(System.getProperty("user.dir"), "engine", "youtube-dl.exe").toAbsolutePath().toString()
            }
            PlatformType.LINUX -> {
                "youtube-dl"
            }
            PlatformType.MAC_OS -> {
                "youtube-dl"
            }
            PlatformType.NOT_SUPPORTED -> {
                logger.error("Not supported OS")
                throw OSException("Not supported OS")
            }
        }
    }

    private fun addProxy(engineBuilder: DownloadEngineArgsBuilder) {
        when (app.config[ContentsUtil.PROXY_TYPE]) {
            ContentsUtil.PROXY_SOCKS -> {
                engineBuilder.add("--proxy",
                        "socks5://${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}/")
            }
            ContentsUtil.PROXY_HTTP -> {
                engineBuilder.add("--proxy",
                        "${app.config[ContentsUtil.PROXY_ADDRESS]}:${app.config[ContentsUtil.PROXY_PORT]}")
            }
        }
    }

    private fun requestJsonArgs(): DownloadEngineArgsBuilder {
        /**
         *  Build args for requesting the json data
         */
        val engineBuilder = DownloadEngineArgsBuilder(core)
        engineBuilder.add("simulator", "-j")
        addProxy(engineBuilder)
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
            if (isRunning && line != null) {
                output.append(line.trim())
            } else {
                break
            }
        }
        return output
    }

    override fun download(formatID: String) {
        isRunning = true
        status = messages["downloading"]
        var line: String?
        val engineBuilder = DownloadEngineArgsBuilder(core)
        addProxy(engineBuilder)
        engineBuilder.add("-f", formatID)
        engineBuilder.add("-o", Paths.get(app.config[ContentsUtil.STORAGE_PATH] as String, outputTemplate).toString())
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

data class YoutubeDLMediaData(val webpage_url: String, val title: String, val description: String, val formats: ArrayList<String>)
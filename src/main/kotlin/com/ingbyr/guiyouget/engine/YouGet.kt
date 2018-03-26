package com.ingbyr.guiyouget.engine

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.utils.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

// todo finish you-get engine logic
class YouGet(override val url: String) : BaseEngine() {
    override val core = initCore()
    override val argsMap = mutableMapOf("core" to core)
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override var speed = "0MiB/s"
    override var extTime = "00:00"
    override var progress = 0.0
    override var status = EngineStatus.ANALYZE
    override var running = false
    private val nameTemplate = "%(title)s.%(ext)s"

    override fun addProxy(proxyType: String, address: String, port: String) {
        when (proxyType) {
            ProxyUtils.SOCKS5 -> {
                argsMap["--proxy"] = "socks5://$address:$port"
            }
            ProxyUtils.HTTP -> {
                argsMap["--proxy"] = "$address:$port"
            }
            ProxyUtils.NONE -> {
            }
        }
    }

    override fun initCore(): String {
        return when (GUIPlatform.current()) {
            GUIPlatformType.WINDOWS -> {
                Paths.get(System.getProperty("user.dir"), "engine", "youtube-dl.exe").toAbsolutePath().toString()
            }
            GUIPlatformType.LINUX -> {
                "youtube-dl"
            }
            GUIPlatformType.MAC_OS -> {
                "youtube-dl"
            }
            GUIPlatformType.NOT_SUPPORTED -> {
                logger.error("Not supported OS")
                throw OSException("Not supported OS")
            }
        }
    }

    override fun execCommand(command: MutableList<String>, downloadType: DownloadType): StringBuilder? {
        /**
         * Exec the command by invoking the system shell etc.
         * Long time method
         */
        running = true
        val builder = ProcessBuilder(command)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?

        when (downloadType) {
            DownloadType.JSON -> {
                // fetch the media json and return string builder
                val output = StringBuilder()
                while (true) {
                    line = r.readLine()
                    if (running && line != null) {
                        output.append(line.trim())
                    } else {
                        break
                    }
                }
                running = false
                return output
            }

            DownloadType.PLAYLIST -> {
                // todo download playlist
            }

            DownloadType.SINGLE -> {
                while (true) {
                    line = r.readLine()
                    if (line != null && running) {
                        parseDownloadStatus(line, DownloadType.SINGLE)
                    } else {
                        if (p != null && p.isAlive) {
                            logger.debug("stop process $p")
                            p.destroyForcibly()
                        }
                        break
                    }
                }
            }

            DownloadType.OTHERS -> {
                // todo tbd
            }

        }
        return null
    }

    override fun fetchMediaJson(): JsonObject {
        argsMap["SimulateJson"] = "-j"
        argsMap["url"] = url
        val mediaData = execCommand(argsMap.build(), DownloadType.JSON)
        if (mediaData != null) {
            try {
                return Parser().parse(mediaData) as JsonObject
            } catch (e: Exception) {
                logger.error(mediaData.toString())
                logger.error(e.toString())
                throw DownloadEngineException("Can not parse media data to json object")
            }
        } else {
            throw DownloadEngineException("Media data is null")
        }
    }

    override fun downloadMedia(formatID: String, outputPath: String) {
        argsMap["-f"] = formatID
        argsMap["-o"] = Paths.get(outputPath, nameTemplate).toString()
        argsMap["url"] = url
        execCommand(argsMap.build(), DownloadType.SINGLE)
    }

    override fun parseDownloadStatus(line: String, downloadType: DownloadType) {
        when (downloadType) {
            DownloadType.SINGLE -> {
                val outs = line.split(" ")
                outs.forEach {
                    if (it.endsWith("%")) progress = it.subSequence(0, it.length - 1).toString().toDouble()
                    if (it.endsWith("/s")) speed = it
                    if (it.matches(Regex("\\d+:\\d+"))) extTime = it
                }
                logger.debug("$progress, $speed, $extTime, $status")
                if (progress >= 100.0) {
                    logger.debug("finished downloading")
                }
            }

            else -> {
                logger.error("not supported yet")
            }
        }
    }

}
package com.ingbyr.guiyouget.engine

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.utils.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set

class YoutubeDL(override val url: String, override val msgQueue: ArrayBlockingQueue<Map<String, Any>>? = null) : BaseEngine() {
    override val core = initCore()
    override val argsMap = mutableMapOf("core" to core)
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var speed = "0MiB/s"
    private var extime = "00:00"
    private var progress = 0.0
    private var status = EngineStatus.ANALYZE
    override var running = AtomicBoolean(false)
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
                logger.debug("no proxy setting in config file")
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
        running.set(true)
        val builder = ProcessBuilder(command)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        val output = StringBuilder()

        var line: String?

        when (downloadType) {
            DownloadType.JSON -> {
                // fetch the media json and return string builder
                while (running.get()) {
                    line = r.readLine()
                    if (line != null) {
                        output.append(line.trim())
                    } else {
                        break
                    }
                }
            }

            DownloadType.PLAYLIST -> {
                // todo download playlist
            }

            DownloadType.SINGLE -> {
                while (running.get()) {
                    line = r.readLine()
                    if (line != null) {
                        parseDownloadStatus(line, DownloadType.SINGLE)
                    } else {
                        break
                    }
                }
            }

            DownloadType.OTHERS -> {
                // todo tbd
            }

        }

        if (p.isAlive) {
            logger.debug("force to stop process $p")
            p.destroyForcibly()
            p.waitFor()
            logger.debug("process stopped")
        }

        return if (!running.get()) { //clear output if stopped by user
            null
        } else {
            running.set(false)
            output
        }
    }

    override fun fetchMediaJson(): JsonObject {
        argsMap["SimulateJson"] = "-j"
        argsMap["url"] = url
        val mediaData = execCommand(argsMap.build(), DownloadType.JSON)
        if (mediaData != null) {
            try {
                return Parser().parse(mediaData) as JsonObject
            } catch (e: Exception) {
                logger.error(e.toString())
                throw DownloadEngineException("parse data failed:\n $mediaData")
            }
        } else {
            logger.error("no media json return from $url")
            throw DownloadEngineException("no media json return from $url")
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
                    if (it.matches(Regex("\\d+:\\d+"))) extime = it
                }
                logger.debug(line)
                logger.debug("$progress, $speed, $extime, $status")
                // send the status to msg queue to update UI
                msgQueue?.put(
                        mapOf("progress" to progress,
                                "speed" to speed,
                                "extime" to extime,
                                "status" to status))
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

//fun main(args: Array<String>) {
//    val ydt = YoutubeDL("https://www.youtube.com/watch?v=4Q2KNl1MAX8")
//    ydt.addProxy(ProxyUtils.SOCKS5, "127.0.0.1", "1080")
//    val json = ydt.fetchMediaJson()
//    println(json.string("title"))
//}

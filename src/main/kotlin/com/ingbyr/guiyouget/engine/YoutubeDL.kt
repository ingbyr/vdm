package com.ingbyr.guiyouget.engine

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.utils.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import kotlin.collections.set


class YoutubeDL(override val url: String, override val msgQueue: ConcurrentLinkedDeque<Map<String, Any>>? = null) : BaseEngine() {
    override val core = initCore()
    override val argsMap = mutableMapOf("core" to core)
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var speed = "0MiB/s"
    private var extime = "00:00"
    private var progress = 0.0
    private var status = EngineStatus.ANALYZE
    override var running = AtomicBoolean(false)
    private val nameTemplate = "%(title)s.%(ext)s"
    private val progressPattern = Pattern.compile("\\d+\\W?\\d*%")
    private val speedPattern = Pattern.compile("\\d+\\W?\\d*\\w+/s")
    private val extimePattern = Pattern.compile("\\d+:\\d+")
    private val sizePattern = Pattern.compile("\\d+\\.\\d+\\w*B\\s") // unused

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
         * Long time task
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

            DownloadType.PLAYLIST -> {
                // todo download playlist
            }
        }

        // wait to clean up auto
        p.waitFor(200, TimeUnit.MICROSECONDS)
        if (p.isAlive) {
            logger.debug("force to stop process $p")
            p.destroyForcibly()
            p.waitFor()
            logger.debug("process stopped")
        }

        return if (running.get()) {
            running.set(false)
            output
        } else {
            //clear output if stopped by user
            null
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
                progress = progressPattern.matcher(line).takeIf { it.find() }?.group()?.toProgess() ?: progress
                speed = speedPattern.matcher(line).takeIf { it.find() }?.group()?.toString() ?: speed
                extime = extimePattern.matcher(line).takeIf { it.find() }?.group()?.toString() ?: extime
                logger.debug("$line -> $progress, $speed, $extime, $status")

                when {
                    progress >= 1.0 -> {
                        status = EngineStatus.FINISH
                        logger.debug("finished task of $url")
                    }
                    progress > 0 -> status = EngineStatus.DOWNLOAD
                    else -> return
                }

                if (!running.get()) {
                    status = EngineStatus.PAUSE
                }

                // send the status to msg queue to update UI
                msgQueue?.offer(
                        mapOf("progress" to progress,
                                "speed" to speed,
                                "extime" to extime,
                                "status" to status))
            }

            DownloadType.PLAYLIST -> {
                //todo download playlist function
            }

            DownloadType.JSON -> {
                //todo no action
            }
        }
    }

    private fun String.toProgess(): Double {
        /**
         * Transfer "42.3%"(String) to 0.423(Double)
         */
        val s = this.replace("%", "")
        return s.trim().toDouble() / 100
    }
}
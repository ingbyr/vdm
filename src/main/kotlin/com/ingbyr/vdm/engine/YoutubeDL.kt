package com.ingbyr.vdm.engine

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.vdm.utils.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class YoutubeDL : AbstractEngine() {

    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override val remoteVersionUrl: String = "https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py"

    private var speed = "0MiB/s"
    private var extime = "00:00"
    private var progress = 0.0
    private var playlistProgress = "0 / 1"
    private var status = EngineStatus.ANALYZE
    private val nameTemplate = "%(title)s.%(ext)s"
    private val progressPattern = Pattern.compile("\\d+\\W?\\d*%")
    private val speedPattern = Pattern.compile("\\d+\\W?\\d*\\w+/s")
    private val extimePattern = Pattern.compile("\\d+:\\d+")
    private val playlistProgressPattern = Pattern.compile("\\d+\\sof\\s\\d+")

    init {
        argsMap["engine"] = when (GUIPlatform.current()) {
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


    override fun parseFormatsJson(json: JsonObject): List<MediaFormat> {
        val title = json.string("title") ?: ""
        val desc = json.string("description") ?: ""
        val formatsJson = json.array<JsonObject>("formats")
        val formats = mutableListOf<MediaFormat>()
        if (formatsJson != null && formatsJson.isNotEmpty()) {
            formatsJson.sortBy {
                it.string("format_id")
            }
            formatsJson.forEachIndexed { index, jsonObject ->
                formats.add(MediaFormat(
                        title = title,
                        desc = desc,
                        vdmTaskID = index,
                        formatID = jsonObject.string("format_id") ?: "",
                        format = jsonObject.string("format") ?: "",
                        formatNote = jsonObject.string("format_note") ?: "",
                        fileSize = jsonObject.long("filesize") ?: 0,
                        ext = jsonObject.string("ext") ?: ""
                ))
            }
        }
        return formats
    }

    override fun url(url: String): AbstractEngine {
        argsMap["url"] = url
        return this
    }

    override fun addProxy(type: ProxyType, address: String, port: String): AbstractEngine {
        when (type) {
            ProxyType.SOCKS5 -> {
                if (address.isEmpty() or port.isEmpty()) {
                    logger.debug("add an empty proxy to youtube-dl")
                    return this
                } else {
                    argsMap["--proxy"] = "socks5://$address:$port"
                }
            }

            ProxyType.HTTP -> {
                if (address.isEmpty() or port.isEmpty()) {
                    logger.debug("add an empty proxy to youtube-dl")
                    return this
                } else {
                    argsMap["--proxy"] = "$address:$port"
                }
            }

            else -> {
            }
        }
        return this
    }

    override fun fetchMediaJson(): JsonObject {
        argsMap["SimulateJson"] = "-j"
        val mediaData = execCommand(argsMap.build(), DownloadType.JSON)
        if (mediaData != null) {
            try {
                return Parser().parse(mediaData) as JsonObject
            } catch (e: Exception) {
                logger.error(e.toString())
                throw DownloadEngineException("parse data failed:\n $mediaData")
            }
        } else {
            logger.error("no media json return")
            throw DownloadEngineException("no media json return")
        }
    }

    override fun format(formatID: String): AbstractEngine {
        return if (formatID.isEmpty()) {
            this
        } else {
            argsMap["-f"] = formatID
            this
        }
    }

    override fun output(outputPath: String): AbstractEngine {
        argsMap["-o"] = Paths.get(outputPath, nameTemplate).toString()
        return this
    }

    override fun downloadMedia(messageQuene: ConcurrentLinkedQueue<Map<String, Any>>) {
        msgQueue = messageQuene
        execCommand(argsMap.build(), DownloadType.SINGLE)
    }

    override fun parseDownloadSingleStatus(line: String) {
        // TODO parse the ffmpeg merging status output
        progress = progressPattern.matcher(line).takeIf { it.find() }?.group()?.toProgress() ?: progress
        speed = speedPattern.matcher(line).takeIf { it.find() }?.group()?.toString() ?: speed
        extime = extimePattern.matcher(line).takeIf { it.find() }?.group()?.toString() ?: extime
        logger.debug("$line -> $progress, $speed, $extime, $status")

        when {
            progress >= 1.0 -> {
                status = EngineStatus.FINISH
                logger.debug("finished single media task of ${argsMap.build()}")
            }
            progress > 0 -> status = EngineStatus.DOWNLOAD
            else -> return
        }

        if (!running.get()) {
            status = EngineStatus.PAUSE
        }

        // send the status to msg queue to update UI
        msgQueue?.offer(mapOf("progress" to progress,
                "speed" to speed,
                "extime" to extime,
                "status" to status))
    }

    override fun parseDownloadPlaylistStatus(line: String) {
        //TODO parse the playlist
        progress = progressPattern.matcher(line).takeIf { it.find() }?.group()?.toProgress() ?: progress
        speed = speedPattern.matcher(line).takeIf { it.find() }?.group()?.toString() ?: speed
        playlistProgress = playlistProgressPattern.matcher(line).takeIf { it.find() }?.group()?.toString()?.replace("of", "/") ?: playlistProgress

        when {
            progress >= 1.0 -> {
                if (playlistProgress.playlistIsCompleted()) {
                    status = EngineStatus.FINISH
                    logger.debug("finished playlist media task of ${argsMap.build()}")
                }
            }
            progress > 0 -> {
                status = EngineStatus.DOWNLOAD
            }
        }

        if (!running.get()) {
            status = EngineStatus.PAUSE
        }

        msgQueue?.offer(
                mapOf("progress" to progress,
                        "speed" to speed,
                        "extime" to playlistProgress,
                        "status" to status))
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
                        parseDownloadSingleStatus(line)
                    } else {
                        break
                    }
                }
            }

            DownloadType.PLAYLIST -> {
                while (running.get()) {
                    line = r.readLine()
                    if (line != null) {
                        //TODO download playlist
                    } else {
                        break
                    }
                }
            }
        }

        // wait to clean up thread
        p.waitFor(200, TimeUnit.MICROSECONDS)
        if (p.isAlive) {
            logger.debug("force to stop process $p")
            p.destroyForcibly()
            p.waitFor()
            logger.debug("stop the process")
        }

        return if (running.get()) {
            running.set(false)
            output
        } else {
            // clear output if stopped by user
            null
        }
    }

    private fun String.toProgress(): Double {
        /**
         * Transfer "42.3%"(String) to 0.423(Double)
         */
        val s = this.replace("%", "")
        return s.trim().toDouble() / 100
    }

    private fun String.playlistIsCompleted(): Boolean {
        /**
         * Compare a / b and return the a>=b
         */
        val progress = this.split("/")
        return progress[0].trim() >= progress[1].trim()
    }

    override fun updateUrl(version: String) = when (GUIPlatform.current()) {
        GUIPlatformType.WINDOWS -> {
            "https://github.com/rg3/youtube-dl/releases/download/$version/youtube-dl.exe"
        }
        GUIPlatformType.LINUX -> {
            "https://github.com/rg3/youtube-dl/releases/download/$version/youtube-dl"
        }
        GUIPlatformType.MAC_OS -> {
            "https://github.com/rg3/youtube-dl/releases/download/$version/youtube-dl"
        }
        GUIPlatformType.NOT_SUPPORTED -> {
            logger.error("Not supported OS")
            ""
        }
    }
}
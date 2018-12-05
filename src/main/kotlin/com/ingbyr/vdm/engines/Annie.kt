package com.ingbyr.vdm.engines

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ingbyr.vdm.dao.searchEngineInfo
import com.ingbyr.vdm.engines.utils.EngineDownloadType
import com.ingbyr.vdm.engines.utils.EngineException
import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskStatus
import com.ingbyr.vdm.models.MediaFormat
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.utils.NetUtils
import com.ingbyr.vdm.utils.OSType
import com.ingbyr.vdm.utils.OSUtils
import com.ingbyr.vdm.utils.UpdateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class Annie : AbstractEngine() {
    companion object : EngineMeteData {
        override val engineInfo = searchEngineInfo(EngineType.ANNIE)
    }

    override val logger: Logger = LoggerFactory.getLogger(Annie::class.java)
    override val downloadNewEngineNeedUnzip: Boolean = true
    override val enginePath: String = engineInfo.execPath
    override val engineType: EngineType = EngineType.ANNIE
    override val argsMap: MutableMap<String, String> = mutableMapOf("engine" to enginePath)
    override val remoteVersionUrl: String = engineInfo.remoteVersionUrl
    override var remoteVersion: String? = null
    override var taskModel: DownloadTaskModel? = null

    private val remoteVersionPattern: Pattern = Pattern.compile("\\d+.+\\d+")
    private var speed = "0MiB/s"
    private var progress = 0.0
    private var size = ""
    private var title = ""
    private val progressPattern = Pattern.compile("\\d+\\.\\d*%")
    private val speedPattern = Pattern.compile("\\d+\\.\\d*\\s+\\w+/s")

    override fun url(url: String): AbstractEngine {
        argsMap["url"] = url
        return this
    }

    override fun addProxy(type: ProxyType, address: String, port: String): AbstractEngine {
        return if (address.isEmpty() or port.isEmpty()) {
            this
        } else {
            when (type) {
                ProxyType.SOCKS5 -> {
                    argsMap["-s"] = "$address:$port"
                }
                ProxyType.HTTP -> {
                    argsMap["-x"] = "http://$address:$port"
                }
                else -> {
                }
            }
            this
        }
    }

    override fun simulateJson(): AbstractEngine {
        argsMap["SimulateJson"] = "-j"
        return this
    }

    override fun fetchMediaJson(): String {
        val mediaData = execCommand(argsMap.build(), EngineDownloadType.JSON)
        if (mediaData != null) {
            return mediaData.toString()
        } else {
            logger.error("no media json return")
            throw EngineException("no media json return")
        }
    }

    override fun format(formatID: String): AbstractEngine {
        if (formatID.isNotEmpty()) argsMap["-f"] = formatID
        return this
    }

    override fun output(outputPath: String): AbstractEngine {
        if (outputPath.isNotEmpty()) argsMap["-o"] = outputPath
        return this
    }

    override fun ffmpegPath(ffmpegPath: String): AbstractEngine {
        return this
    }

    override fun cookies(cookies: String): AbstractEngine {
        if (cookies.isNotEmpty()) {
            argsMap["-c"] = cookies
        }
        return this
    }

    override fun downloadMedia(downloadTaskModel: DownloadTaskModel, message: ResourceBundle) {
        taskModel = downloadTaskModel
        taskModel?.run {
            execCommand(argsMap.build(), EngineDownloadType.SINGLE)
        }
    }

    override fun parseDownloadOutput(line: String) {
        if (title.isEmpty() && line.trim().startsWith("Title")) {
            title = line.trim().removePrefix("Title:").trim()
            if (title.isNotEmpty()) taskModel?.title = title
        }
        if (size.isEmpty() && line.trim().startsWith("Size")) {
            size = line.trim().removePrefix("Size:").trim().split("(").firstOrNull()?.trim() ?: ""
            if (size.isNotEmpty()) taskModel?.size = size.trim()
        }

        progress = progressPattern.matcher(line).takeIf { it.find() }?.group()?.toProgress() ?: progress
        speed = speedPattern.matcher(line).takeIf { it.find() }?.group()?.toString() ?: speed
        logger.debug("$line -> title=$title, progress=$progress, size=$size, speed=$speed")

        taskModel?.run {
            if (this@Annie.progress >= 1.0) {
                this.progress = 1.0
                if (line.trim().startsWith("[ffmpeg]"))
                    this.status = DownloadTaskStatus.MERGING
                else
                    this.status = DownloadTaskStatus.COMPLETED
            } else if (this@Annie.progress > 0) {
                this.progress = this@Annie.progress
                this.status = DownloadTaskStatus.DOWNLOADING
            }
        }
    }

    override fun execCommand(command: MutableList<String>, downloadType: EngineDownloadType): StringBuilder? {
        running.set(true)
        val builder = ProcessBuilder(command)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream, charset))
        logger.debug("parse output as $charset")
        val output = StringBuilder()
        var line: String?
        when (downloadType) {
            EngineDownloadType.JSON -> {
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

            EngineDownloadType.SINGLE, EngineDownloadType.PLAYLIST -> {
                while (running.get()) {
                    line = r.readLine()
                    if (line != null) {
                        parseDownloadOutput(line)
                    } else {
                        break
                    }
                }
            }
        }

        if (p.isAlive) { // means user stop this models manually
            p.destroy()
            p.waitFor(200, TimeUnit.MICROSECONDS)
        }

        if (p.isAlive) {
            p.destroyForcibly()
        }

        return if (running.get()) {
            running.set(false)
            output
        } else { // means user stop this models manually
            taskModel?.run {
                status = DownloadTaskStatus.STOPPED
            }
            logger.debug("stop the models of $taskModel")
            null
        }
    }

    override fun parseFormatsJson(jsonString: String): List<MediaFormat> {
        val formats = mutableListOf<MediaFormat>()
        val annieMediaJson = jacksonObjectMapper().readValue<AnnieMediaJson>(jsonString)
        annieMediaJson.formats.forEach { id, formatInfo ->
            formats.add(
                MediaFormat(
                    title = annieMediaJson.title,
                    desc = "",
                    formatID = id,
                    format = formatInfo.quality,
                    formatNote = "",
                    fileSize = formatInfo.size.toLong(),
                    ext = formatInfo.urls.first().ext
                )
            )
        }
        return formats
    }

    override fun updateUrl(): String = when (OSUtils.currentOS) {
        OSType.WINDOWS -> {
            "https://github.com/iawia002/annie/releases/download/$remoteVersion/annie_${remoteVersion}_Windows_32-bit.zip"
        }
        OSType.LINUX -> {
            "https://github.com/iawia002/annie/releases/download/$remoteVersion/annie_${remoteVersion}_Linux_32-bit.tar.gz"
        }
        OSType.MAC_OS -> {
            "https://github.com/iawia002/annie/releases/download/$remoteVersion/annie_${remoteVersion}_macOS_32-bit.tar.gz"
        }
    }

    override fun existNewVersion(localVersion: String): Boolean {
        val remoteVersionData = NetUtils().get(remoteVersionUrl)
        return if (remoteVersionData?.isNotEmpty() == true) {
            remoteVersion = remoteVersionPattern.matcher(remoteVersionData).takeIf { it.find() }?.group()
            if (remoteVersion != null) {
                logger.debug("[$engineType] local version $localVersion, remote version $remoteVersion")
                UpdateUtils.check(localVersion, remoteVersion!!)
            } else {
                logger.error("[$engineType] get remote version failed")
                false
            }
        } else {
            false
        }
    }
}


data class AnnieMediaJson(
    @JsonProperty("Site") val site: String = "",
    @JsonProperty("Title") val title: String = "",
    @JsonProperty("Type") val type: String = "",
    @JsonProperty("Formats") val formats: Map<String, Format>
) {
    data class Format(
        @JsonProperty("URLs") val urls: List<URL> = listOf(),
        @JsonProperty("Quality") val quality: String = "",
        @JsonProperty("Size") val size: Int = 0
    ) {

        data class URL(
            @JsonProperty("URL") val uRL: String = "",
            @JsonProperty("Size") val size: Int = 0,
            @JsonProperty("Ext") val ext: String = ""
        )
    }
}
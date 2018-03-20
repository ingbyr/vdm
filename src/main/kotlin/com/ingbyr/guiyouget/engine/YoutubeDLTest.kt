package com.ingbyr.guiyouget.engine

import com.ingbyr.guiyouget.utils.GUIPlatform
import com.ingbyr.guiyouget.utils.GUIPlatformType
import com.ingbyr.guiyouget.utils.OSException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

enum class EngineStatus {
    /**
     * Different download engine status
     */
    ANALYZE,
    DOWNLOAD,
    PAUSE,
    RESUME,
    FAIL
}

enum class ProxyType {
    HTTP,
    SOCKS5
}

enum class DownloadType {
    JSON,
    SINGLE,
    PALTLIST,
    OTHERS
}

abstract class BaseEngine {
    /**
     * Adapt to the different platform
     * Build the args for the command line
     * Exec the command line
     * Parse the output and update the main UI thread
     * Stop thread
     */
    abstract val core: String
    abstract val logger: Logger
    abstract var speed: String
    abstract var extTime: String
    abstract var status: EngineStatus
    abstract var running: Boolean
    abstract val argsMap: MutableMap<String, String>
    abstract val url: String

    abstract fun initCore(): String // handle with the different platform
    // todo change return type to json
    abstract fun fetchMediaJson(): String // fetch the json data of the url

    abstract fun downloadMedia(formatID: String) // download media
    abstract fun parseDownloadStatus(line: String, downloadType: DownloadType)
    abstract fun addProxy(proxyType: ProxyType, address: String, port: String)
    abstract fun execCommand(command: MutableList<String>, downloadType: DownloadType): StringBuilder?


    fun MutableMap<String, String>.build(): MutableList<String> {
        /**
         * Generate the command line from argsMap
         */
        val args = mutableListOf<String>()
        this.forEach {
            if (it.key.startsWith("-")) {
                args.add(it.key)
                args.add(it.value)
            } else {
                args.add(it.value)
            }
        }
        logger.debug("exec $args")
        return args
    }

}

class YoutubeDLTest(override val url: String) : BaseEngine() {
    override val core = initCore()
    override val argsMap = mutableMapOf("core" to core)
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override var speed = "0MiB/s"
    override var extTime = "00:00"
    override var status = EngineStatus.ANALYZE
    override var running = false

    override fun addProxy(proxyType: ProxyType, address: String, port: String) {
        when (proxyType) {
            ProxyType.SOCKS5 -> {
                argsMap["--proxy"] = "socks5://$address:$port"
            }
            ProxyType.HTTP -> {
                argsMap["--proxy"] = "$address:$port"
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
            DownloadType.PALTLIST -> {

            }
            DownloadType.SINGLE -> {

            }
            DownloadType.OTHERS -> {

            }
        }
        return null
    }

    override fun fetchMediaJson(): String {
        // todo use fastjson lib to parse data
        // todo change fuel lib
        // todo delete klaxon lib
        argsMap["SimulateJson"] = "-j"
        argsMap["url"] = url
        val mediaJson = execCommand(argsMap.build(), DownloadType.JSON)
        logger.debug(mediaJson.toString())
        return mediaJson.toString()
    }

    override fun downloadMedia(formatID: String) {

    }

    override fun parseDownloadStatus(line: String, downloadType: DownloadType) {
    }
}

fun main(args: Array<String>) {
    val engine = YoutubeDLTest("https://www.youtube.com/watch?v=sFni3t0UFJI")
    engine.addProxy(ProxyType.SOCKS5, "127.0.0.1", "1080")
    engine.fetchMediaJson()
}

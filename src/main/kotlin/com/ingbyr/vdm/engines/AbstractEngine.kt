package com.ingbyr.vdm.engines

import com.ingbyr.vdm.engines.utils.EngineDownloadType
import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.MediaFormat
import com.ingbyr.vdm.models.ProxyType
import org.slf4j.Logger
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractEngine {
    var charset = "UTF-8"

    protected abstract val logger: Logger
    protected val running: AtomicBoolean = AtomicBoolean(false)
    abstract val downloadNewEngineNeedUnzip: Boolean
    abstract val enginePath: String
    abstract val engineType: EngineType
    abstract val argsMap: MutableMap<String, String>
    abstract val remoteVersionUrl: String
    abstract var remoteVersion: String?
    abstract var version: String
    abstract var taskModel: DownloadTaskModel?

    abstract fun url(url: String): AbstractEngine
    abstract fun simulateJson(): AbstractEngine
    abstract fun addProxy(type: ProxyType, address: String, port: String): AbstractEngine
    abstract fun fetchMediaJson(): String
    abstract fun format(formatID: String): AbstractEngine
    abstract fun output(outputPath: String): AbstractEngine
    abstract fun ffmpegPath(ffmpegPath: String): AbstractEngine
    abstract fun cookies(cookies: String): AbstractEngine
    abstract fun downloadMedia(downloadTaskModel: DownloadTaskModel, message: ResourceBundle)
    abstract fun parseDownloadOutput(line: String)
    abstract fun execCommand(command: MutableList<String>, downloadType: EngineDownloadType): StringBuilder?
    abstract fun parseFormatsJson(jsonString: String): List<MediaFormat>

    abstract fun engineExecPath(): String
    abstract fun updateUrl(): String
    abstract fun existNewVersion(localVersion: String): Boolean

    open fun stopTask() {
        /**
         * Please overwrite this if need extra operation.
         */
        running.set(false)
    }

    protected fun MutableMap<String, String>.build(): MutableList<String> {
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

    protected fun String.toProgress(): Double {
        /**
         * Transfer "42.3%"(String) to 0.423(Double)
         */
        val s = this.replace("%", "")
        return s.trim().toDouble() / 100
    }
}
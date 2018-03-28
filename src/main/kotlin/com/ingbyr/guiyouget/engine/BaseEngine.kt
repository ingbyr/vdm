package com.ingbyr.guiyouget.engine

import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.utils.DownloadType
import com.ingbyr.guiyouget.utils.EngineStatus
import org.slf4j.Logger
import tornadofx.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean


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
    abstract var running: AtomicBoolean
    abstract val argsMap: MutableMap<String, String>
    abstract val url: String
    abstract val msgQueue: ArrayBlockingQueue<Map<String, Any>>?

    abstract fun initCore(): String // handle with the different platform
    abstract fun fetchMediaJson(): JsonObject // fetch the json data of the url
    abstract fun downloadMedia(formatID: String, outputPath: String) // download media
    abstract fun parseDownloadStatus(line: String, downloadType: DownloadType)
    abstract fun addProxy(proxyType: String, address: String, port: String)
    abstract fun execCommand(command: MutableList<String>, downloadType: DownloadType): StringBuilder?

    fun stop() {
        running.set(false)
    }

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
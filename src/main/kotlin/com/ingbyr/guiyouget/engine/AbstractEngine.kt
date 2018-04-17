package com.ingbyr.guiyouget.engine

import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.utils.DownloadType
import com.ingbyr.guiyouget.utils.ProxyType
import org.slf4j.Logger
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicBoolean


abstract class AbstractEngine {
    /**
     * Adapt to the different platform
     * Build the args for the command line
     * Exec the command line
     * Parse the output and update the main UI thread
     * Stop thread
     */
    protected abstract val logger: Logger
    protected var msgQueue: ConcurrentLinkedDeque<Map<String, Any>>? = null
    protected val running: AtomicBoolean = AtomicBoolean(false)
    protected val argsMap: MutableMap<String, String> = mutableMapOf()

    abstract fun url(url: String): AbstractEngine
    abstract fun addProxy(type: ProxyType, address: String, port:String): AbstractEngine
    abstract fun fetchMediaJson(): JsonObject
    abstract fun format(formatID: String): AbstractEngine
    abstract fun output(outputPath: String): AbstractEngine
    abstract fun downloadMedia(messageQuene: ConcurrentLinkedDeque<Map<String, Any>>)

    abstract fun parseDownloadSingleStatus(line: String)
    abstract fun parseDownloadPlaylistStatus(line: String)
    abstract fun execCommand(command: MutableList<String>, downloadType: DownloadType): StringBuilder?

    fun stopTask() {
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

    fun displayCommand() {
        /**
         * Only for debug
         */
        println(argsMap.build())
    }
}
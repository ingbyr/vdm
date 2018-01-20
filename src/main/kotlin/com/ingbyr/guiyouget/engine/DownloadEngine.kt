package com.ingbyr.guiyouget.engine

import org.slf4j.LoggerFactory

class DownloadEngine(val core: String) {
    private val logger = LoggerFactory.getLogger(DownloadEngine::class.java)
    private val argsMap = mutableMapOf<String, String>()


    fun add(key: String, value: String) {
        argsMap.put(key, value)
    }

    // Build args except engine arg
    fun build(): MutableList<String> {
        val args = mutableListOf(core)
        argsMap.forEach {
            if (it.key.startsWith("-")) {
                args.add(it.key)
                args.add(it.value)
            } else {
                args.add(it.value)
            }
        }
        logger.debug("run command $args")
        return args
    }
}
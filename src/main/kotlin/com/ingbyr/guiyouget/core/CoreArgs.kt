package com.ingbyr.guiyouget.core

import org.slf4j.LoggerFactory

class CoreArgs(val core: String) {
    val logger = LoggerFactory.getLogger(CoreArgs::class.java)
    val argsMap = mutableMapOf<String, String>()


    fun add(key: String, value: String) {
        argsMap.put(key, value)
    }

    // Build args except core arg
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
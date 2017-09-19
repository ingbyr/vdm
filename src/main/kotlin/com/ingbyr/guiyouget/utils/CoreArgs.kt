package com.ingbyr.guiyouget.utils

class CoreArgs(core: String) {
    val argsMap = mutableMapOf<String, String>()
    val args = mutableListOf(core)

    fun add(key: String, value: String) {
        argsMap.put(key, value)
    }

    // Build args except core arg
    fun build(): MutableList<String> {
        argsMap.forEach {
            if (it.key.startsWith("-")) {
                args.add(it.key)
                args.add(it.value)
            } else {
                args.add(it.value)
            }
        }
//        println(args)
        return args
    }
}
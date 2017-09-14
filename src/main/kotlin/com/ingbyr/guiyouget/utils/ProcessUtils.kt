package com.ingbyr.guiyouget.utils

import java.io.BufferedReader
import java.io.InputStreamReader


object ProcessUtils {
    fun runCommand(core: String, args: Array<String>): StringBuilder {
        val output = StringBuilder()
        val builder = ProcessBuilder(core, *args)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?
        while (true) {
            line = r.readLine()
            if (line == null) {
                break
            }
            output.append(line.trim())
        }
        return output
    }
}
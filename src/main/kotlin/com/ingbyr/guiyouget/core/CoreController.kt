package com.ingbyr.guiyouget.core

import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


abstract class CoreController : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/core")
    }

    fun runCommand(args: MutableList<String>): StringBuilder {
        val output = StringBuilder()
        val builder = ProcessBuilder(args)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream, charset("GBK")))
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

    abstract fun runDownloadCommand(formatID: String)
    abstract fun parseStatus(line: String)
}
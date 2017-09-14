package com.ingbyr.guiyouget.utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser


object YoutubeDLUtils {
    val core = this::class.java.getResource("/core/youtube-dl.exe").path
    val parser: Parser = Parser()

    fun getMediaInfo(args: Array<String>): JsonObject {
        val output = ProcessUtils.runCommand(core, args)
//        Files.write(Paths.get(System.getProperty("user.dir"), "info.json"), output.toString().toByteArray())
        val json = parser.parse(output) as JsonObject
        return json
    }
}
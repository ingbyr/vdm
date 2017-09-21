package com.ingbyr.guiyouget.utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.models.Progress
import java.nio.file.Files
import java.nio.file.Paths

object YouGet {
    val NAME = "YOU_GET"
    val core = this::class.java.getResource("/core/you-get.exe").path
    val parser = Parser()

    fun getMediaInfo(args: MutableList<String>): JsonObject {
        val output = ProcessUtils.runCommand(args)
//        Files.write(Paths.get(System.getProperty("user.dir"), "info-youget.json"), output.toString().toByteArray())
        return parser.parse(output) as JsonObject
    }


    fun downloadMedia(pg: Progress, args: MutableList<String>) {
        ProcessUtils.runDownloadCommand(pg, args)
    }

    fun requestJsonAargs(url: String): CoreArgs {
        val args = CoreArgs(YouGet.core)
        args.add("simulator", "--json")
        args.add("url", url)
        return args
    }
}
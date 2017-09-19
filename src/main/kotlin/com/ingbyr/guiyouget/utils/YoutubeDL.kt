package com.ingbyr.guiyouget.utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.controllers.MediaListController
import java.nio.file.Files
import java.nio.file.Paths


object YoutubeDL {
    val NAME = "YOUTUBE_DL"
    val core = this::class.java.getResource("/core/youtube-dl.exe").path
    val parser = Parser()

    fun getMediaInfo(args: MutableList<String>): JsonObject {
        val output = ProcessUtils.runCommand(args)
//        Files.write(Paths.get(System.getProperty("user.dir"), "info.json"), output.toString().toByteArray())
        return parser.parse(output) as JsonObject
    }

    fun downloadMedia(mediaListController: MediaListController, args: MutableList<String>) {
        ProcessUtils.runDownloadCommand(mediaListController, args)
    }
}
package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.events.DownloadMediaRequest
import com.ingbyr.guiyouget.models.Progress
import com.ingbyr.guiyouget.utils.CoreArgs
import com.ingbyr.guiyouget.utils.YoutubeDL
import tornadofx.*

class ProgressController : Controller() {
    private val core = config.string("core", YoutubeDL.NAME)

    fun download(pg: Progress, request: DownloadMediaRequest) {
        if (core == YoutubeDL.NAME) {
            try {
                val args = CoreArgs(YoutubeDL.core)
                args.add("-f", request.formatID)
                args.add("--proxy", "socks5://127.0.0.1:1080/")
                args.add("url", request.url)
                YoutubeDL.downloadMedia(pg, args.build())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
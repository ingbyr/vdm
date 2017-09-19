package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.events.LoadMediaListRequest
import com.ingbyr.guiyouget.utils.CoreArgs
import com.ingbyr.guiyouget.utils.YoutubeDL
import tornadofx.*
import java.nio.file.Paths

class MainController : Controller() {
    val storagePath = config.string("storagePath", Paths.get("").toAbsolutePath().toString())
    val core = config.string("core", YoutubeDL.NAME)


    // args参数应该由该controller构建
    fun requestMediaInfo(url: String) {
        if (core == YoutubeDL.NAME) {
            val args = CoreArgs(YoutubeDL.core)
            args.add("simulator", "-j")
            args.add("--proxy", "socks5://127.0.0.1:1080/")
            args.add("url", url)
//            log.info(args.build().toString())
            fire(LoadMediaListRequest(args.build()))
        }
    }

    fun updateCore() {
        // TODO: check updates
        // https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py
    }
}
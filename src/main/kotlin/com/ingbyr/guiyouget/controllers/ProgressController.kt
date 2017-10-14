package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.core.YouGet
import com.ingbyr.guiyouget.core.YoutubeDL
import com.ingbyr.guiyouget.events.DownloadingRequestWithYouGet
import com.ingbyr.guiyouget.events.DownloadingRequestWithYoutubeDL
import com.ingbyr.guiyouget.events.ResumeDownloading
import com.ingbyr.guiyouget.utils.CoreUtils
import org.slf4j.LoggerFactory
import tornadofx.*

class ProgressController : Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var youget: YouGet? = null
    private var youtubedl: YoutubeDL? = null
    private var formatID: String? = null
    private var coreType: String? = null

    fun subscribeEvents() {
        subscribe<DownloadingRequestWithYoutubeDL> {
            youtubedl = it.youtubedl
            formatID = it.formatID
            coreType = CoreUtils.YOUTUBE_DL
            it.youtubedl.runDownloadCommand(it.formatID)
        }

        subscribe<DownloadingRequestWithYouGet> {
            youget = it.youget
            formatID = it.formatID
            coreType = CoreUtils.YOU_GET
            it.youget.runDownloadCommand(it.formatID)
        }

        subscribe<ResumeDownloading> {
            when (coreType) {
                CoreUtils.YOUTUBE_DL -> youtubedl?.runDownloadCommand(formatID!!)
                CoreUtils.YOU_GET -> youget?.runDownloadCommand(formatID!!)
            }
        }
    }
}

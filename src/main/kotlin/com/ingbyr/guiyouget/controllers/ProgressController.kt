package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.engine.YouGet
import com.ingbyr.guiyouget.engine.YoutubeDL
import com.ingbyr.guiyouget.events.DownloadingRequestWithYouGet
import com.ingbyr.guiyouget.events.DownloadingRequestWithYoutubeDL
import com.ingbyr.guiyouget.events.ResumeDownloading
import org.slf4j.LoggerFactory
import tornadofx.*

class ProgressController : Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var youget: YouGet? = null
    private var youtubedl: YoutubeDL? = null
    private var formatID: String? = null

    fun subscribeEvents() {
        subscribe<DownloadingRequestWithYoutubeDL> {
            youtubedl = it.youtubedl
            formatID = it.formatID
            it.youtubedl.runDownloadCommand(it.formatID)
        }

        subscribe<DownloadingRequestWithYouGet> {
            youget = it.youget
            formatID = it.formatID
            it.youget.runDownloadCommand(it.formatID)
        }

        subscribe<ResumeDownloading> {
          youtubedl?.let {
              logger.debug("resume downloading with youtube-dl")
              it.runDownloadCommand(formatID!!)
          }
          youget?.let {
              logger.debug("resume downloading with youget")
              it.runDownloadCommand(formatID!!)
          }
        }
    }
}

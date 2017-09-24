package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.core.OkHttpController
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYouGet
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYoutubeDL
import com.ingbyr.guiyouget.events.UpdateStates
import com.ingbyr.guiyouget.utils.CoreUtils
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.File

class UpdatesController: Controller() {
    val logger = LoggerFactory.getLogger(this::class.java)
    val okhttp = OkHttpController()

    fun subscribeEvents() {
        subscribe<RequestCheckUpdatesYouGet> {
            fire(UpdateStates("Check for updates of you-get"))
            val remoteJson = okhttp.requestJson(CoreUtils.REMOTE_CONF_URL)
            if (remoteJson != null) {
                val youget = remoteJson["youget"] as String
                logger.debug("get remote version file url $youget")
                doUpdates(CoreUtils.YOU_GET, youget)
            } else {
                //todo update failed
            }
        }

        subscribe<RequestCheckUpdatesYoutubeDL> {
            fire(UpdateStates("Check for updates of youyube-dl"))
            val remoteJson = okhttp.requestJson(CoreUtils.REMOTE_CONF_URL)
            if (remoteJson != null) {
                val youtubedl = remoteJson["youtubedl"] as String
                logger.debug("get remote version file url $youtubedl")
                doUpdates(CoreUtils.YOUTUBE_DL, youtubedl)
            } else {
                //todo update failed
            }
        }
    }

    private fun doUpdates(core: String, url: String) {
        val vStr = okhttp.requestString(url)
        val v = Regex("'\\d+.+'").findAll(vStr.toString()).toList().flatMap(MatchResult::groupValues)
        val remoteVersion = v.first().substring(1, v.first().length - 1)
        when (core) {
            CoreUtils.YOU_GET -> {
                val localVersion = app.config["you-get-version"] as String
                logger.debug("remote version is $remoteVersion, local version is $localVersion")
                if (remoteVersion > localVersion) {
                    // do updates
                    fire(UpdateStates("New version $remoteVersion of you-get, downloading"))
                    val url = CoreUtils.yougetUpdateURL(remoteVersion)
                    logger.debug("you-get update url $url")
                    okhttp.downloadFile(url, File(MainController::class.java.getResource("/core/you-get.exe").toURI()))
                }
            }
            CoreUtils.YOUTUBE_DL -> {
                val localVersion = app.config["youtube-dl-version"] as String
                if (remoteVersion > localVersion) {
                    // do updates
                    fire(UpdateStates("New version $remoteVersion of youtube-dl, downloading"))
                    val url = CoreUtils.youtubedlUpdateURL(remoteVersion)
                    logger.debug("youtube-dl update url $url")
                    okhttp.downloadFile(url, File(MainController::class.java.getResource("/core/youtube-dl.exe").toURI()))
                }
            }
        }

    }
}
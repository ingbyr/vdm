package com.ingbyr.guiyouget.controllers

import com.ingbyr.guiyouget.utils.OkHttpController
import com.ingbyr.guiyouget.events.*
import com.ingbyr.guiyouget.utils.ContentsUtil
import org.slf4j.LoggerFactory
import tornadofx.*
import java.nio.file.Paths
import java.util.*

class UpdatesController : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/UpdatesView")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val okhttp = OkHttpController()

    fun subscribeEvents() {
        subscribe<RequestCheckUpdatesYouGet> {
            fire(UpdateYouGetStates(messages["checkForUpdates"]))
            val remoteJson = okhttp.requestJson(ContentsUtil.REMOTE_CONF_URL)
            if (remoteJson != null) {
                val youget = remoteJson["youget"] as String
                logger.debug("[you-get] remote version url $youget")
                doUpdates(ContentsUtil.YOU_GET, youget)
            } else {
                fire(UpdateYouGetStates(messages["failToUpdate"]))
                logger.error("remote updating json is null")
            }
        }

        subscribe<RequestCheckUpdatesYoutubeDL> {
            fire(UpdateYoutubeDLStates(messages["checkForUpdates"]))
            val remoteJson = okhttp.requestJson(ContentsUtil.REMOTE_CONF_URL)
            if (remoteJson != null) {
                val youtubedl = remoteJson["youtubedl"] as String
                logger.debug("[youtube-dl] remote version url $youtubedl")
                doUpdates(ContentsUtil.YOUTUBE_DL, youtubedl)
            } else {
                fire(UpdateYoutubeDLStates(messages["failToUpdate"]))
                logger.error("remote updating json is null")
            }
        }
    }

    private fun doUpdates(core: String, url: String) {
        val vStr = okhttp.requestString(url)
        val v = Regex("'\\d+.+'").findAll(vStr.toString()).toList().flatMap(MatchResult::groupValues)
        val remoteVersion = v.first().substring(1, v.first().length - 1)
        when (core) {
            ContentsUtil.YOU_GET -> {
                val localVersion = app.config["you-get-version"] as String
                logger.debug("[you-get] remote version is $remoteVersion, local version is $localVersion")
                if (remoteVersion > localVersion) {
                    // do updates
                    fire(UpdateYouGetStates("${messages["newVersionIs"]} $remoteVersion, ${messages["downloading"]}"))
                    val url = ContentsUtil.yougetUpdateURL(remoteVersion)
                    logger.debug("[you-get] update url $url")
                    okhttp.downloadFile(url,
                            Paths.get(System.getProperty("user.dir"), "engine", "you-get.exe").toString(),
                            ContentsUtil.YOU_GET_VERSION,
                            remoteVersion)
                } else {
                    fire(UpdateYouGetStates(messages["noUpdates"]))
                    logger.debug("[you-get] no updates")
                }
            }
            ContentsUtil.YOUTUBE_DL -> {
                val localVersion = app.config["youtube-dl-version"] as String
                logger.debug("[youtube-dl] remote version is $remoteVersion, local version is $localVersion")
                if (remoteVersion > localVersion) {
                    // do updates
                    fire(UpdateYoutubeDLStates("${messages["newVersionIs"]} $remoteVersion, ${messages["downloading"]}"))
                    val url = ContentsUtil.youtubedlUpdateURL(remoteVersion)
                    logger.debug("[youtube-dl] update url $url")
                    okhttp.downloadFile(url,
                            Paths.get(System.getProperty("user.dir"), "engine", "youtube-dl.exe").toString(),
                            ContentsUtil.YOUTUBE_DL_VERSION,
                            remoteVersion)
                } else {
                    fire(UpdateYoutubeDLStates(messages["noUpdates"]))
                    logger.debug("[youtube-dl] no updates")
                }
            }
        }
    }
}
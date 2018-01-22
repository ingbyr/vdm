package com.ingbyr.guiyouget.controllers

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYouGet
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYoutubeDL
import com.ingbyr.guiyouget.events.UpdateYouGetStates
import com.ingbyr.guiyouget.events.UpdateYoutubeDLStates
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

    fun subscribeEvents() {

        subscribe<RequestCheckUpdatesYouGet> {
            fire(UpdateYouGetStates(messages["checkForUpdates"]))
            Fuel.get(ContentsUtil.REMOTE_YOU_GET_VERSION).responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        val remoteV = parseVersion(result.get())
                        if (needUpdate(app.config[ContentsUtil.YOU_GET_VERSION] as String, remoteV)) {
                            logger.debug("[you-get] try to download new version")
                            downloadYouGet(remoteV)
                        } else {
                            fire(UpdateYouGetStates(messages["noUpdates"]))
                        }
                    }
                    is Result.Failure -> {
                        fire(UpdateYouGetStates(messages["failToUpdate"]))
                        logger.error("[you-get] failed to check the remote version")
                    }
                }
            }
        }

        subscribe<RequestCheckUpdatesYoutubeDL> {
            fire(UpdateYoutubeDLStates(messages["checkForUpdates"]))
            Fuel.get(ContentsUtil.REMOTE_YOUTUBE_DL_VERSION).responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        val remoteV = parseVersion(result.get())
                        if (needUpdate(app.config[ContentsUtil.YOUTUBE_DL_VERSION] as String, remoteV)) {
                            logger.debug("[youtube-dl] try to download new version")
                            downloadYoutubeDL(remoteV)
                        } else {
                            fire(UpdateYoutubeDLStates(messages["noUpdates"]))
                        }
                    }
                    is Result.Failure -> {
                        fire(UpdateYoutubeDLStates(messages["failToUpdate"]))
                        logger.error("[youtube-dl] failed to check the remote version")
                    }
                }
            }
        }
    }

    private fun needUpdate(localVersion: String, remoteVersion: String): Boolean {
        logger.debug("local version: $localVersion, remote version: $remoteVersion")
        val lv = localVersion.split(".").map { it.toInt() }
        val rv = remoteVersion.split(".").map { it.toInt() }
        return rv.indices.any { rv[it] - lv[it] > 0 }
    }

    private fun parseVersion(vStr: String): String {
        val v = Regex("'\\d+.+'").findAll(vStr).toList().flatMap(MatchResult::groupValues)
        return v.first().replace("'", "").replace("\"", "")
    }


    private fun downloadYouGet(remoteV: String) {
        val url = ContentsUtil.yougetUpdateURL(remoteV)
        logger.debug("[you-get] downloading from $url")
        Fuel.get(url).response { _, response, _ ->
            logger.debug("[you-get] downloading from ${response.url}")
            Fuel.download(response.url.toString()).destination { _, _ ->
                Paths.get(System.getProperty("user.dir"), "engine", "you-get.exe").toFile()
            }.progress { readBytes, totalBytes ->
                        fire(UpdateYouGetStates("${messages["newVersionIs"]} $remoteV, ${messages["downloading"]} ${(readBytes.toFloat() / totalBytes.toFloat() * 100).toInt()}%"))
                    }.response { _, _, result ->
                        when (result) {
                            is Result.Success -> {
                                app.config[ContentsUtil.YOU_GET_VERSION] = remoteV
                                app.config.save()
                                logger.debug("[you-get] finished updating")
                                fire(UpdateYouGetStates(messages["compeleted"]))
                            }
                            is Result.Failure -> {
                                logger.debug("[you-get] failed to update")
                                fire(UpdateYouGetStates(messages["noUpdates"]))
                            }
                        }
                    }
        }
    }

    private fun downloadYoutubeDL(remoteV: String) {
        val url = ContentsUtil.youtubedlUpdateURL(remoteV)
        logger.debug("[youtube-dl] downloading from $url")
        Fuel.get(url).response { _, response, _ ->
            logger.debug("[youtube-dl] downloading from ${response.url}")
            Fuel.download(response.url.toString()).destination { _, _ ->
                Paths.get(System.getProperty("user.dir"), "engine", "youtube-dl.exe").toFile()
            }.progress { readBytes, totalBytes ->
                        fire(UpdateYoutubeDLStates("${messages["newVersionIs"]} $remoteV, ${messages["downloading"]} ${(readBytes.toFloat() / totalBytes.toFloat() * 100).toInt()}%"))
                    }.response { _, _, result ->
                        when (result) {
                            is Result.Success -> {
                                app.config[ContentsUtil.YOUTUBE_DL_VERSION] = remoteV
                                app.config.save()
                                logger.debug("[youtube-dl] finished updating")
                                fire(UpdateYoutubeDLStates(messages["compeleted"]))
                            }
                            is Result.Failure -> {
                                logger.debug("[youtube-dl] failed to update")
                                fire(UpdateYoutubeDLStates(messages["noUpdates"]))
                            }
                        }
                    }
        }
    }
}
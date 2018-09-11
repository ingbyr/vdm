package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskType
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.models.TaskConfig
import com.ingbyr.vdm.utils.AppConfigUtils
import com.ingbyr.vdm.utils.AppProperties
import tornadofx.*

class CreateDownloadTaskController: Controller() {

    private val cu = AppConfigUtils(app.config)
    val downloadDefaultFormat = cu.load(AppProperties.DOWNLOAD_DEFAULT_FORMAT).toBoolean()

    fun createDownloadTaskInstance(url: String): DownloadTaskModel {
        val engineType = EngineType.valueOf(cu.load(AppProperties.ENGINE_TYPE))
        val storagePath = cu.load(AppProperties.STORAGE_PATH)

        val ffmpeg = cu.load(AppProperties.FFMPEG_PATH)
        val cookie = "" // TODO support cookie

        val taskConfig = TaskConfig(
                url, engineType, DownloadTaskType.SINGLE_MEDIA,
                downloadDefaultFormat, storagePath, cookie, ffmpeg)

        val proxyType = ProxyType.valueOf(cu.load(AppProperties.PROXY_TYPE))
        when (proxyType) {
            ProxyType.SOCKS5 -> {
                taskConfig.proxy(proxyType, cu.load(AppProperties.SOCKS5_PROXY_ADDRESS), cu.load(AppProperties.SOCKS5_PROXY_PORT))
            }
            ProxyType.HTTP -> {
                taskConfig.proxy(proxyType, cu.load(AppProperties.SOCKS5_PROXY_ADDRESS), cu.load(AppProperties.SOCKS5_PROXY_PORT))
            }
            ProxyType.NONE -> {
                taskConfig.proxy(proxyType, "", "")
            }
        }
        return DownloadTaskModel(taskConfig)
    }

    private fun prepareCookieFile() {
        // cookie 列表
    }
}
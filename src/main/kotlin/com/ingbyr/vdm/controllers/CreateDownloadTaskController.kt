package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskType
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.models.TaskConfig
import com.ingbyr.vdm.utils.Attributes
import com.ingbyr.vdm.utils.config.proxy
import tornadofx.*

class CreateDownloadTaskController : Controller() {

    val downloadDefaultFormat =
        app.config.boolean(Attributes.DOWNLOAD_DEFAULT_FORMAT, Attributes.Defaults.DOWNLOAD_DEFAULT_FORMAT)

    fun createDownloadTaskInstance(url: String): DownloadTaskModel {
        val engineType = EngineType.valueOf(app.config.string(Attributes.ENGINE_TYPE, EngineType.YOUTUBE_DL.name))
        val storagePath = app.config.string(Attributes.STORAGE_PATH, Attributes.Defaults.STORAGE_PATH)

        val ffmpeg = app.config.string(Attributes.FFMPEG_PATH, Attributes.Defaults.FFMPEG_PATH)
        val cookie = "" // TODO support cookie

        val taskConfig = TaskConfig(
            url, engineType, DownloadTaskType.SINGLE_MEDIA,
            downloadDefaultFormat, storagePath, cookie, ffmpeg
        )

        val proxyType = app.config.proxy(Attributes.PROXY_TYPE)
        when (proxyType) {
            ProxyType.SOCKS5 -> {
                taskConfig.proxy(
                    proxyType,
                    app.config.proxy(Attributes.SOCKS5_PROXY_ADDRESS).name,
                    app.config.proxy(Attributes.SOCKS5_PROXY_PORT).name
                )
            }
            ProxyType.HTTP -> {
                taskConfig.proxy(
                    proxyType,
                    app.config.string(Attributes.SOCKS5_PROXY_ADDRESS, Attributes.Defaults.SOCKS5_PROXY_ADDRESS),
                    app.config.string(Attributes.SOCKS5_PROXY_PORT, Attributes.Defaults.SOCKS5_PROXY_PORT)
                )
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
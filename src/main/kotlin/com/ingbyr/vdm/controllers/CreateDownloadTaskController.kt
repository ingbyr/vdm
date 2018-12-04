package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskType
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.models.TaskConfig
import com.ingbyr.vdm.utils.ConfigUtils
import com.ingbyr.vdm.utils.Attributes
import tornadofx.*

class CreateDownloadTaskController: Controller() {

    val downloadDefaultFormat = ConfigUtils.load(Attributes.DOWNLOAD_DEFAULT_FORMAT).toBoolean()

    fun createDownloadTaskInstance(url: String): DownloadTaskModel {
        val engineType = EngineType.valueOf(ConfigUtils.load(Attributes.ENGINE_TYPE))
        val storagePath = ConfigUtils.load(Attributes.STORAGE_PATH)

        val ffmpeg = ConfigUtils.load(Attributes.FFMPEG_PATH)
        val cookie = "" // TODO support cookie

        val taskConfig = TaskConfig(
                url, engineType, DownloadTaskType.SINGLE_MEDIA,
                downloadDefaultFormat, storagePath, cookie, ffmpeg)

        val proxyType = ProxyType.valueOf(ConfigUtils.load(Attributes.PROXY_TYPE))
        when (proxyType) {
            ProxyType.SOCKS5 -> {
                taskConfig.proxy(proxyType, ConfigUtils.load(Attributes.SOCKS5_PROXY_ADDRESS), ConfigUtils.load(Attributes.SOCKS5_PROXY_PORT))
            }
            ProxyType.HTTP -> {
                taskConfig.proxy(proxyType, ConfigUtils.load(Attributes.SOCKS5_PROXY_ADDRESS), ConfigUtils.load(Attributes.SOCKS5_PROXY_PORT))
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
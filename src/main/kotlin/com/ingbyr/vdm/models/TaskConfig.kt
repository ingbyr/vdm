package com.ingbyr.vdm.models

import com.ingbyr.vdm.engines.utils.EngineType

data class TaskConfig(
        var url: String,
        val engineType: EngineType,
        val downloadType: DownloadTaskType,
        val downloadDefaultFormat: Boolean,
        val storagePath: String,

        val cookie: String = "",
        val ffmpeg: String = "",
        var formatId: String = "",
        var proxyType: ProxyType = ProxyType.NONE,
        var proxyAddress: String = "",
        var proxyPort: String = "") {

    fun proxy(type: ProxyType, address: String, port: String) {
        this.proxyType = type
        this.proxyAddress = address
        this.proxyPort = port
    }
}

enum class ProxyType {
    SOCKS5,
    HTTP,
    NONE
}
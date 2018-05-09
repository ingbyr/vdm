package com.ingbyr.vdm.utils

/**
 * Instance for the config file
 */
data class VDMConfig(val engineType: EngineType, val proxyType: ProxyType, val address: String, val port: String,
                     val downloadDefaultFormat: Boolean, val storagePath: String, val cookieString: String)


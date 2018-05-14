package com.ingbyr.vdm.utils

import java.io.Serializable

enum class ProxyType {
    SOCKS5,
    HTTP,
    NONE
}

data class VDMProxy(val proxyType: ProxyType, val address: String = "", val port: String = "") : Serializable
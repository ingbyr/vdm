package com.ingbyr.vdm.utils

enum class ProxyType {
    SOCKS5,
    HTTP,
    NONE
}

data class VDMProxy(val proxyType: ProxyType, val address: String, val port: String)
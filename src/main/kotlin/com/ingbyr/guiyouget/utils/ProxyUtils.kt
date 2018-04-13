package com.ingbyr.guiyouget.utils

// todo delete this
object ProxyUtils {
    const val TYPE = "proxy-type"
    const val ADDRESS = "proxy-address"
    const val PORT = "proxy-port"
    const val HTTP = "http"
    const val SOCKS5 = "socks5"
    const val NONE = "None"
}

enum class ProxyType {
    PROXY_TYPE,
    SOCKS5,
    HTTP,
    SOCKS5_PROXY_ADDRESS,
    SOCKS5_PROXY_PORT,
    HTTP_PROXY_ADDRESS,
    HTTP_PROXY_PORT,
    NONE
}


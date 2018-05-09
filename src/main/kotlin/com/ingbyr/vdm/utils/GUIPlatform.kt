package com.ingbyr.vdm.utils

enum class GUIPlatformType {
    WINDOWS,
    LINUX,
    MAC_OS,
    NOT_SUPPORTED
}

object GUIPlatform {
    fun current(): GUIPlatformType {
        val platform = System.getProperties()["os.name"]?.toString() ?: "Unknown"

        return when {
            platform.startsWith("Linux") -> GUIPlatformType.LINUX
            platform.startsWith("Win") -> GUIPlatformType.WINDOWS
            platform.startsWith("MAC") -> GUIPlatformType.MAC_OS
            else -> GUIPlatformType.NOT_SUPPORTED
        }
    }
}

class OSException(override var message: String) : Exception(message)
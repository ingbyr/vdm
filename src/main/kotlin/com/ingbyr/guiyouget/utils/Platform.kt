package com.ingbyr.guiyouget.utils

enum class PlatformType {
    WINDOWS,
    LINUX,
    MAC_OS,
    NOT_SUPPORTED
}

object Platform {
    fun current(): PlatformType {
        val platform = System.getProperties()["os.name"]?.toString() ?: "Unknown"

        return when {
            platform.startsWith("Linux") -> PlatformType.LINUX
            platform.startsWith("Win") -> PlatformType.WINDOWS
            platform.startsWith("MAC") -> PlatformType.MAC_OS
            else -> PlatformType.NOT_SUPPORTED
        }
    }
}

class OSException(override var message: String) : Exception(message)
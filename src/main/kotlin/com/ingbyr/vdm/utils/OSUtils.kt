package com.ingbyr.vdm.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.nio.file.Paths

enum class OSType {
    WINDOWS,
    LINUX,
    MAC_OS,
}

object OSUtils {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val platform = System.getProperties()["os.name"]?.toString() ?: "Unknown"
    val currentOS: OSType = when {
        platform.startsWith("Linux") -> OSType.LINUX
        platform.startsWith("Win") -> OSType.WINDOWS
        platform.startsWith("MAC") -> OSType.MAC_OS
        else -> throw OSException("not supported os")
    }

    fun openDir(pathStr: String) {
        val file = Paths.get(pathStr).toFile()
        when (currentOS) {
            OSType.LINUX -> {
                Runtime.getRuntime().exec("xdg-open $file")
            }
            OSType.WINDOWS -> {
                Desktop.getDesktop().open(file)
            }
            OSType.MAC_OS -> {
                Desktop.getDesktop().open(file)
            }
        }
    }
}

class OSException(override var message: String) : Exception(message)
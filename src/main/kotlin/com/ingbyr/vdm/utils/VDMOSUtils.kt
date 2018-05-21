package com.ingbyr.vdm.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.nio.file.Paths

enum class VDMOSType {
    WINDOWS,
    LINUX,
    MAC_OS,
}

object VDMOSUtils {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val platform = System.getProperties()["os.name"]?.toString() ?: "Unknown"
    var currentOS: VDMOSType

    init {
        currentOS = when {
            platform.startsWith("Linux") -> VDMOSType.LINUX
            platform.startsWith("Win") -> VDMOSType.WINDOWS
            platform.startsWith("MAC") -> VDMOSType.MAC_OS
            else -> throw VDMOSException("not supported os")
        }
    }

    fun openDir(pathStr: String) {
        val file = Paths.get(pathStr).toFile()
        when (currentOS) {
            VDMOSType.LINUX -> {
                Runtime.getRuntime().exec("xdg-open $file")
            }
            VDMOSType.WINDOWS -> {
                Desktop.getDesktop().open(file)
            }
            VDMOSType.MAC_OS -> {
                Desktop.getDesktop().open(file)
            }
        }
    }
}

class VDMOSException(override var message: String) : Exception(message)
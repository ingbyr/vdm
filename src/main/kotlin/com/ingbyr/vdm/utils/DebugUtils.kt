package com.ingbyr.vdm.utils

import ch.qos.logback.classic.Level
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

object DebugUtils {

    private val logger: Logger = LoggerFactory.getLogger(DebugUtils.javaClass)
    private val prop: Properties = System.getProperties()
    private val rootLogger:ch.qos.logback.classic.Logger = LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger

    fun changeDebugMode(enable: Boolean) {
        if (enable) {
            rootLogger.level = Level.DEBUG
            showOSInfo()
        }else {
            rootLogger.level = Level.ERROR
        }
    }

    fun showOSInfo() {
        logger.debug("OS: ${prop["os.name"]?.toString()} Arch: ${prop["os.arch"]?.toString()} Version: ${prop["os.version"]?.toString()}")
        logger.debug("JAVA: ${prop["java.version"]?.toString()} Vendor: ${prop["java.vendor"]?.toString()}")
        logger.debug("Default Locale: ${FX.locale} Current Locale:${Locale.getDefault().language}_${Locale.getDefault().country}")
        logger.debug("Save config file to ${AppProperties.configFilePath}")
    }
}
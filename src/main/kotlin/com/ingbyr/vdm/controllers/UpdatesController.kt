package com.ingbyr.vdm.controllers

import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*


class UpdatesController : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/UpdatesView")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun subscribeEvents() {
        //TODO engine needs to handle itself updates func
    }

    private fun needUpdate(localVersion: String, remoteVersion: String): Boolean {
        logger.debug("local version: $localVersion, remote version: $remoteVersion")
        val lv = localVersion.split(".").map { it.toInt() }
        val rv = remoteVersion.split(".").map { it.toInt() }
        return rv.indices.any { rv[it] - lv[it] > 0 }
    }

    private fun parseVersion(vStr: String): String {
        val v = Regex("'\\d+.+'").findAll(vStr).toList().flatMap(MatchResult::groupValues)
        return v.first().replace("'", "").replace("\"", "")
    }
}
package com.ingbyr.vdm.utils

import org.slf4j.LoggerFactory
import kotlin.math.max

object UpdateUtils {

    private val log = LoggerFactory.getLogger(UpdateUtils::class.java)

    fun check(localVersion: String, remoteVersion: String): Boolean {
        try {
            val lv: List<Int> = localVersion.split(".").map { it.toInt() }
            val rv: List<Int> = remoteVersion.split(".").map { it.toInt() }
            val count = max(lv.size, rv.size)
            for (i in 0 until count) {
                if (rv.getOrElse(i) { 0 } - lv.getOrElse(i) { 0 } > 0) return true
                else if (rv.getOrElse(i) { 0 } - lv.getOrElse(i) { 0 } < 0) return false
            }
        } catch (e: Exception) {
            log.error(e.toString())
        }
        return false
    }
}
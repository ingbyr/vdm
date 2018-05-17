package com.ingbyr.vdm.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    fun time2String(dateTime: LocalDateTime): String {
        return dateTime.format(formatter)
    }

    fun string2time(dateTime: String): LocalDateTime {
        return LocalDateTime.parse(dateTime, formatter)
    }
}
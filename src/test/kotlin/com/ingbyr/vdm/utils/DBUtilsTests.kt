package com.ingbyr.vdm.utils

import org.junit.jupiter.api.Test

class DBUtilsTests {
    @Test
    fun `load all download task`() {
        println(DBUtils.loadAllDownloadTasks())
    }
}
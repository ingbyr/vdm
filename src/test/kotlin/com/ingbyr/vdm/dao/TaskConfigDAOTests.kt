package com.ingbyr.vdm.dao

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.DownloadTaskType
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.utils.AppProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class TaskConfigDAOTests {
    init {
        Database.connect(AppProperties.DATABASE_URL, driver = "org.h2.Driver", user = "vdm", password = "vdm")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(TaskConfigTable)
        }
    }

    @Test
    fun `insert task config to db`() {
        transaction {
            TaskConfigDAO.new {
                url = "test url"
                downloadType = DownloadTaskType.SINGLE_MEDIA.name
                engineType = EngineType.YOUTUBE_DL.name
                downloadDefaultFormat = false
                storagePath = AppProperties.APP_DIR.toString()
                cookie = "test cookie"
                ffmpeg = "test ffmpeg"
                formatId = "test format id"
                proxyType = ProxyType.SOCKS5.name
                proxyAddress = "127.0.0.1"
                proxyPort = "1080"
            }
        }
    }
}
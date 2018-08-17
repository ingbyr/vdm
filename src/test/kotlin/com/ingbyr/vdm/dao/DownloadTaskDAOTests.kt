package com.ingbyr.vdm.dao

import com.ingbyr.vdm.models.DownloadTaskStatus
import com.ingbyr.vdm.utils.AppProperties
import com.ingbyr.vdm.utils.DateTimeUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class DownloadTaskDAOTests {
    init {
        Database.connect(AppProperties.DATABASE_URL, driver = "org.h2.Driver", user = "vdm", password = "vdm")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(DownloadTaskTable)
        }
    }

    @Test
    fun `insert download task to db`() {
        transaction {
            DownloadTaskDAO.new {
                taskConfig = -1
                checked=false
                title="db test"
                size="1024 mb"
                status = DownloadTaskStatus.ANALYZING.name
                progress = 0.0F
                createdAt = DateTimeUtils.nowTimeString()
            }
        }
    }
}
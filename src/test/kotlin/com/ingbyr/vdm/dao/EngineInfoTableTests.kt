package com.ingbyr.vdm.dao

import com.ingbyr.vdm.utils.Attributes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EngineInfoTableTests {
    @BeforeEach
    fun `setup database`() {
        Database.connect(Attributes.DATABASE_URL, driver = "org.h2.Driver")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(EngineInfoTable)
        }
    }

    @Test
    fun `create engine info`() {
        transaction {
            EngineInfo.new {
                name = "test-engine"
                execPath = "test-engine-exec-path"
                localVersion = "0.0.0"
                remoteVersionUrl = "http://test-engine-remote-version-url"
            }
            Assertions.assertFalse(EngineInfo.find { EngineInfoTable.name eq "test-engine" }.empty())
        }
    }
}
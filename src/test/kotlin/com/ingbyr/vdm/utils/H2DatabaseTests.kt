package com.ingbyr.vdm.utils

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.junit.jupiter.api.Test
import java.sql.DriverManager

class H2DatabaseTests {

    private val dbUri = "jdbc:h2:~/.vdm/h2db"

    @Test
    fun `connect to h2 database`() {
        val conn = DriverManager.getConnection(dbUri, "vdm", "vdm")
        conn.use {
            val jooq = DSL.using(conn, SQLDialect.H2)
            jooq.createTable("DownloadTask").column("title", SQLDataType.VARCHAR.length(255)).execute()
            println(jooq.meta().tables)
        }
    }
}
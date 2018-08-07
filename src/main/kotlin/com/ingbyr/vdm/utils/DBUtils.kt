package com.ingbyr.vdm.utils

import com.ingbyr.vdm.models.DownloadTaskModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DBUtils {

    private val log = LoggerFactory.getLogger(DBUtils::class.java)
    private val db = Database.connect(AppProperties.DATABASE_URL, driver = "org.h2.Driver", user = "vdm", password = "vdm")


    // TODO handle with db

    /**
     *  init database
     */
    fun initDatabase() {
        transaction {
            addLogger(StdOutSqlLogger)

        }
    }

    fun saveDownloadTask(taskID: String, downloadTask: DownloadTaskModel) {
        log.debug("save download models to db")
    }

    fun deleteDownloadTask(downloadTask: DownloadTaskModel) {
        log.debug("delete download models from db")
    }

    fun loadAllDownloadTasks(): List<DownloadTaskModel> {
        log.debug("load all download tasks")
        return listOf()
    }

    fun closeDB() {

    }
}
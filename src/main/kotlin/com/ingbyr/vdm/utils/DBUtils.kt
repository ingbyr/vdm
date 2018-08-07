package com.ingbyr.vdm.utils

import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.tables.DownloadTaskTable
import com.ingbyr.vdm.tables.TaskEngineConfigTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DBUtils {

    private val log = LoggerFactory.getLogger(DBUtils::class.java)

    init {
        Database.connect(AppProperties.DATABASE_URL, driver = "org.h2.Driver", user = "vdm", password = "vdm")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(TaskEngineConfigTable, DownloadTaskTable)
        }
    }


    // TODO handle with db
    fun saveDownloadTask(taskID: String, downloadTask: DownloadTaskModel) {
        log.debug("save download models to db")
        val config = downloadTask.taskEngineConfig
        transaction {
            // save taskEngineConfig
            val configId = (TaskEngineConfigTable.insert {
                it[engineType] = config.engineType.name
                it[proxyType] = config.proxy.proxyType.name
                it[proxyAddress] = config.proxy.address
                it[proxyPort] = config.proxy.port
                it[downloadDefaultFormat] = config.downloadDefaultFormat
                it[storagePath] = config.storagePath
                it[cookie] = config.cookie
                it[ffmpeg] = config.ffmpeg
            } get TaskEngineConfigTable.id)!!
            log.debug("taskEngineConfigId is $configId")

            DownloadTaskTable.insert {
                it[id] = taskID
                it[taskEngineConfig] = configId
                it[url] = downloadTask.url
                it[checked] = downloadTask.checked
                it[title] = downloadTask.title
                it[size] = downloadTask.size
                it[status] = downloadTask.status.name
                it[progress] = downloadTask.progress.toFloat()
                it[createdAt] = DateTimeUtils.time2String(downloadTask.createdAt)
                it[formatID] = downloadTask.formatID
                it[type] = downloadTask.type.name
            }
        }
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
package com.ingbyr.vdm.utils

import com.ingbyr.vdm.dao.*
import com.ingbyr.vdm.models.DownloadTaskModel
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DBUtils {

    private val log = LoggerFactory.getLogger(DBUtils::class.java)

    init {
        Database.connect(AppProperties.DATABASE_URL, driver = "org.h2.Driver", user = "vdm", password = "vdm")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(TaskConfigTable, DownloadTaskTable)
        }
    }


    // TODO handle with db
    fun saveDownloadTask(downloadTask: DownloadTaskModel) {
        log.debug("save download models to db")
        val tc = downloadTask.taskConfig
        transaction {
            // save taskConfig
            val taskConfigDB = TaskConfigDAO.new {
                url = tc.url
                downloadType = tc.downloadType.name
                engineType = tc.engineType.name
                downloadDefaultFormat = tc.downloadDefaultFormat
                storagePath = tc.storagePath
                cookie = tc.cookie
                ffmpeg = tc.ffmpeg
                formatId = tc.formatId
                proxyType = tc.proxyType.name
                proxyAddress = tc.proxyPort
                proxyPort = tc.proxyPort
            }

            DownloadTaskDAO.new {
                taskConfig = taskConfigDB.id
                checked = downloadTask.checked
                title = downloadTask.title
                size = downloadTask.size
                status = downloadTask.status.name
                progress = downloadTask.progress.toFloat()
                createdAt = downloadTask.createdAt
            }
        }
    }

    fun deleteDownloadTask(downloadTask: DownloadTaskModel) {
        log.debug("delete download models from db")
        transaction {
            val downloadTaskDB = DownloadTaskDAO.find {
                DownloadTaskTable.createdAt eq downloadTask.createdAt
            }.firstOrNull()
            downloadTaskDB?.delete()
        }
    }

    fun loadAllDownloadTasks(): MutableList<DownloadTaskModel> {
        log.debug("load all download tasks")
        val tasks = mutableListOf<DownloadTaskModel>()
        transaction {
            val loadTaskConfig = {id:EntityID<Int> -> TaskConfigDAO.findById(id)?.trans()}
            DownloadTaskDAO.all().forEach {
                tasks.add(it.trans(loadTaskConfig))
            }
        }
        return tasks
    }
}
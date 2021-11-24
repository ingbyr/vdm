package com.ingbyr.vdm.utils

import com.ingbyr.vdm.dao.*
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.TaskConfig
import javafx.collections.ObservableList
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DBUtils {

    private val log = LoggerFactory.getLogger(DBUtils::class.java)

    init {
        Database.connect(Attributes.DATABASE_URL, driver = "org.h2.Driver", user = "vdm", password = "vdm")
        transaction {
//            addLogger(StdOutSqlLogger) // TODO enable db logger
            SchemaUtils.create(TaskConfigTable, DownloadTaskTable)
        }
    }


    fun saveDownloadTask(newDownloadTaskModel: DownloadTaskModel) {
        val newTaskConfig = newDownloadTaskModel.taskConfig
        transaction {
            val oldDownloadTask = DownloadTaskDAO.find { DownloadTaskTable.createdAt eq newDownloadTaskModel.createdAt }.firstOrNull()
            if (oldDownloadTask != null) {
                // update task app
                updateTaskConfigInDB(oldDownloadTask.taskConfig, newTaskConfig)
                // update download task model
                updateDownloadTaskModelInDB(oldDownloadTask, newDownloadTaskModel)
            } else {
                // create new one and save to db
                val taskConfigDB = createTaskConfigInDB(newTaskConfig)
                createDownloadTaskModelInDB(newDownloadTaskModel, taskConfigDB)
            }
        }

    }

    /**
     * must be wrapped in transaction block
     */
    private fun updateTaskConfigInDB(oldTaskConfig: TaskConfigDAO, newTaskConfig: TaskConfig) {
        log.debug("update download task app data in db")
        // update task app
        oldTaskConfig.url = newTaskConfig.url
        oldTaskConfig.downloadType = newTaskConfig.downloadType.name
        oldTaskConfig.engineType = newTaskConfig.engineType.name
        oldTaskConfig.downloadDefaultFormat = newTaskConfig.downloadDefaultFormat
        oldTaskConfig.storagePath = newTaskConfig.storagePath
        oldTaskConfig.cookie = newTaskConfig.cookie
        oldTaskConfig.ffmpeg = newTaskConfig.ffmpeg
        oldTaskConfig.proxyType = newTaskConfig.proxyType.name
        oldTaskConfig.proxyAddress = newTaskConfig.proxyAddress
        oldTaskConfig.proxyPort = newTaskConfig.proxyPort
    }

    /**
     * must be wrapped in transaction block
     */
    private fun updateDownloadTaskModelInDB(oldDownloadTaskModel: DownloadTaskDAO, newDownloadTaskModel: DownloadTaskModel) {
        log.debug("update download task data in db")
        oldDownloadTaskModel.checked = newDownloadTaskModel.checked
        oldDownloadTaskModel.title = newDownloadTaskModel.title
        oldDownloadTaskModel.size = newDownloadTaskModel.size
        oldDownloadTaskModel.status = newDownloadTaskModel.status.name
        oldDownloadTaskModel.progress = newDownloadTaskModel.progress.toFloat()
        oldDownloadTaskModel.createdAt = newDownloadTaskModel.createdAt
    }

    /**
     * must be wrapped in transaction block
     */
    private fun createTaskConfigInDB(taskConfig: TaskConfig) = TaskConfigDAO.new {
        log.debug("create task app and save to db")
        url = taskConfig.url
        downloadType = taskConfig.downloadType.name
        engineType = taskConfig.engineType.name
        downloadDefaultFormat = taskConfig.downloadDefaultFormat
        storagePath = taskConfig.storagePath
        cookie = taskConfig.cookie
        ffmpeg = taskConfig.ffmpeg
        formatId = taskConfig.formatId
        proxyType = taskConfig.proxyType.name
        proxyAddress = taskConfig.proxyPort
        proxyPort = taskConfig.proxyPort
    }

    private fun createDownloadTaskModelInDB(downloadTask: DownloadTaskModel, taskConfigDAO: TaskConfigDAO) = DownloadTaskDAO.new {
        log.debug("create download task model and save to db")
        taskConfig = taskConfigDAO
        checked = downloadTask.checked
        title = downloadTask.title
        size = downloadTask.size
        status = downloadTask.status.name
        progress = downloadTask.progress.toFloat()
        createdAt = downloadTask.createdAt
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

    fun loadAllDownloadTasks(downloadTaskModelList: ObservableList<DownloadTaskModel>) {
        log.debug("load all download tasks")
        transaction {
            DownloadTaskDAO.all().sortedBy { it.createdAt }.forEach {
                downloadTaskModelList.add(it.toModel())
            }
        }
    }
}
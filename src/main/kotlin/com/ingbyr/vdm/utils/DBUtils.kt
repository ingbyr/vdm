package com.ingbyr.vdm.utils

import com.ingbyr.vdm.dao.DownloadTaskTable
import com.ingbyr.vdm.dao.TaskConfigTable
import com.ingbyr.vdm.models.DownloadTaskModel
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
        val taskConfig = downloadTask.taskConfig
        transaction {
            // save taskConfig

        }
    }

    fun deleteDownloadTask(downloadTask: DownloadTaskModel) {
        log.debug("delete download models from db")
        transaction {
            //            val task = DownloadTaskTable.slice(DownloadTaskTable.taskEngineConfig).select {
//                DownloadTaskTable.id eq DateTimeUtils.time2String(downloadTask.createdAt)
//            }.firstOrNull()
//            if (task != null) {
//                val taskEngineConfigId = task[DownloadTaskTable.taskEngineConfig]
//                TaskConfigTable.deleteWhere { TaskConfigTable.id eq taskEngineConfigId }
//                DownloadTaskTable.deleteWhere {
//                    DownloadTaskTable.id eq DateTimeUtils.time2String(downloadTask.createdAt)
//                }
//            }
        }
    }

    fun loadAllDownloadTasks(): List<DownloadTaskModel> {
        log.debug("load all download tasks")
        val tasks = mutableListOf<DownloadTaskModel>()
//        transaction {
//            DownloadTaskTable.selectAll().forEach {
//                val taskEngineConfig = TaskConfigTable.select {
//                    TaskConfigTable.id eq it[DownloadTaskTable.taskEngineConfig]
//                }.firstOrNull()
//                if (taskEngineConfig != null) {
//                    val config = TaskConfig(
//                            EngineType.valueOf(taskEngineConfig[TaskConfigTable.engineType]),
//                            VDMProxy(ProxyType.valueOf(taskEngineConfig[TaskConfigTable.proxyType]), taskEngineConfig[TaskConfigTable.proxyAddress], taskEngineConfig[TaskConfigTable.proxyPort]),
//                            taskEngineConfig[TaskConfigTable.downloadDefaultFormat],
//                            taskEngineConfig[TaskConfigTable.storagePath],
//                            taskEngineConfig[TaskConfigTable.cookie],
//                            taskEngineConfig[TaskConfigTable.ffmpeg]
//                    )
//                    val task  = DownloadTaskModel(
//                            config,
//                            it[DownloadTaskTable.url],
//                            DateTimeUtils.string2time(it[DownloadTaskTable.createdAt]),
//                            it[DownloadTaskTable.formatID],
//                            it[DownloadTaskTable.checked],
//                            it[DownloadTaskTable.title],
//                            it[DownloadTaskTable.size],
//                            it[DownloadTaskTable.progress].toDouble(),
//                            DownloadTaskStatus.valueOf(it[DownloadTaskTable.status]),
//                            DownloadTaskType.valueOf(it[DownloadTaskTable.type])
//                    )
//                    tasks.add(task)
//                }
//            }
//        }
        return tasks
    }

    fun closeDB() {

    }
}
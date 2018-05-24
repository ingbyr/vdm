package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engine.AbstractEngine
import com.ingbyr.vdm.engine.utils.EngineFactory
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.events.StopBackgroundTask
import com.ingbyr.vdm.events.UpdateEngineTask
import com.ingbyr.vdm.models.DownloadTaskData
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.utils.*
import org.mapdb.DBMaker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class MainController : Controller() {

    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val db = DBMaker.fileDB(VDMUtils.DATABASE_PATH_STR).transactionEnable().make()
    @Suppress("UNCHECKED_CAST")
    private val downloadTaskData = db.hashMap(VDMUtils.DB_DOWNLOAD_TASKS).createOrOpen() as MutableMap<String, DownloadTaskData>
    val downloadTaskModelList = mutableListOf<DownloadTaskModel>().observable()
    private val engineList = ConcurrentHashMap<LocalDateTime, AbstractEngine>()

    init {
        subscribe<StopBackgroundTask> {
            if (it.stopAll) {
                logger.debug("try to stop all download tasks")
                engineList.forEach {
                    it.value.stopTask()
                }
            } else if (it.downloadTask != null) {
                logger.debug("try to stop download task ${it.downloadTask}")
                engineList[it.downloadTask.createdAt]?.stopTask()
            }
        }

        subscribe<CreateDownloadTask> {
            logger.debug("create task: ${it.downloadTask}")
            addTaskToList(it.downloadTask)
            // save to db
            saveTaskToDB(it.downloadTask)
        }

        // background thread
        subscribe<UpdateEngineTask> {
            val engine = EngineFactory.create(it.engineType)
            val vdmConfig = VDMConfig(it.engineType, VDMProxy(ProxyType.NONE), false, engine!!.enginePath)
            val downloadTask = DownloadTaskModel(vdmConfig, "", LocalDateTime.now(), title = "[${messages["ui.update"]} ${it.engineType.name}] ")
            downloadTaskModelList.add(downloadTask)
            if (engine.existNewVersion(it.localVersion)) {
                downloadTask.url = engine.updateUrl()
                NetUtils().downloadEngine(downloadTask, engine.remoteVersion!!)
            } else {
                downloadTask.title += messages["ui.noAvailableUpdates"]
                downloadTask.size = ""
                downloadTask.status = messages["ui.completed"]
                downloadTask.progress = 1.0
            }
        }
    }


    fun startDownloadTask(downloadTask: DownloadTaskModel) {
        downloadTask.status = messages["ui.analyzing"]
        runAsync {
            // download
            val engine = EngineFactory.create(downloadTask.vdmConfig.engineType)
            engine?.run {
                engineList[downloadTask.createdAt] = this
                this.url(downloadTask.url).addProxy(downloadTask.vdmConfig.proxy).format(downloadTask.formatID).output(downloadTask.vdmConfig.storagePath).downloadMedia(downloadTask, messages)
            }
        }
    }

    fun startAllDownloadTask() {
        downloadTaskModelList.forEach {
            if (it.status != messages["ui.completed"]) {
                startDownloadTask(it)
            }
        }
    }

    fun deleteTask(taskItem: DownloadTaskModel) {
        downloadTaskModelList.removeAll(taskItem)
        downloadTaskData.remove(DateTimeUtils.time2String(taskItem.createdAt))
    }

    /**
     * Key of map is "yyyy-MM-dd HH:mm:ss.SSS" which is defined in DateTimeUtils.kt
     * Value of map is a instance of DownloadTask
     */
    private fun saveTaskToDB(taskItem: DownloadTaskData) {
        val taskID = DateTimeUtils.time2String(taskItem.createdAt!!)
        logger.debug("add task $taskID to download task db")
        downloadTaskData[taskID] = taskItem
    }

    private fun addTaskToList(taskItem: DownloadTaskData) {
        val downloadTaskModel = taskItem.toModel()
        downloadTaskModelList.add(downloadTaskModel)
        startDownloadTask(downloadTaskModel)
    }

    fun loadTaskFromDB() {
        downloadTaskData.forEach {
            downloadTaskModelList.add(it.value.toModel())
        }

        downloadTaskModelList.sortBy {
            it.createdAt
        }
    }

    fun updateVDM() {
        // TODO update VDM like as youtube-dl rules
        hostServices.showDocument(VDMUtils.VDM_UPDATE_URL)
    }

    fun clear() {
        downloadTaskModelList.forEach {
            downloadTaskData[DateTimeUtils.time2String(it.createdAt)] = it.toData()
        }
        db.commit()
        db.close()
    }
}
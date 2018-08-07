package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.AbstractEngine
import com.ingbyr.vdm.engines.utils.EngineFactory
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.events.UpdateEngineTask
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskStatus
import com.ingbyr.vdm.models.DownloadTaskType
import com.ingbyr.vdm.models.TaskEngineConfig
import com.ingbyr.vdm.utils.*
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
    val downloadTaskModelList = mutableListOf<DownloadTaskModel>().observable()
    private val engineList = ConcurrentHashMap<LocalDateTime, AbstractEngine>() // FIXME auto clean the finished models engines

    init {
        subscribe<CreateDownloadTask> {
            logger.debug("create models: ${it.downloadTask}")
            // TODO for debug. need uncomment this
            // addTaskToList(it.downloadTask)
            saveTaskToDB(it.downloadTask)
        }

        // background thread
        subscribe<UpdateEngineTask> {
            val engine = EngineFactory.create(it.engineType)
            val vdmConfig = TaskEngineConfig(it.engineType, VDMProxy(ProxyType.NONE), false, engine!!.enginePath)
            val downloadTask = DownloadTaskModel(vdmConfig, "", LocalDateTime.now(), title = "[${messages["ui.update"]} ${it.engineType.name}] ", type = DownloadTaskType.ENGINE)
            downloadTaskModelList.add(downloadTask)

            try {
                if (engine.existNewVersion(it.localVersion)) {
                    downloadTask.url = engine.updateUrl()
                    logger.info("update the ${downloadTask.taskEngineConfig.engineType} from ${downloadTask.url}")
                    NetUtils().downloadEngine(downloadTask, engine.remoteVersion!!)
                } else {
                    downloadTask.title += messages["ui.noAvailableUpdates"]
                    downloadTask.size = ""
                    downloadTask.status = DownloadTaskStatus.COMPLETED
                    downloadTask.progress = 1.0
                }
            } catch (e: Exception) {
                logger.error(e.toString())
                downloadTask.status = DownloadTaskStatus.FAILED
            }
        }
    }


    fun startTask(downloadTask: DownloadTaskModel) {
        if (downloadTask.type == DownloadTaskType.ENGINE) return
        downloadTask.status = DownloadTaskStatus.ANALYZING
        runAsync {
            // download
            val engine = EngineFactory.create(downloadTask.taskEngineConfig.engineType)
            engine?.run {
                engineList[downloadTask.createdAt] = this
                this.url(downloadTask.url).addProxy(downloadTask.taskEngineConfig.proxy).format(downloadTask.formatID).output(downloadTask.taskEngineConfig.storagePath).cookies(downloadTask.taskEngineConfig.cookie).ffmpegPath(downloadTask.taskEngineConfig.ffmpeg).downloadMedia(downloadTask, messages)
            }
        }
    }

    fun startAllTask() {
        downloadTaskModelList.forEach {
            if (it.status != DownloadTaskStatus.COMPLETED && it.type != DownloadTaskType.ENGINE) {
                startTask(it)
            }
        }
    }

    fun stopTask(downloadTask: DownloadTaskModel) {
        logger.debug("try to stop download models $downloadTask")
        engineList[downloadTask.createdAt]?.stopTask()
    }

    fun stopAllTask() {
        logger.debug("try to stop all download tasks")
        engineList.forEach {
            it.value.stopTask()
        }
    }

    fun deleteTask(downloadTaskModel: DownloadTaskModel) {
        stopTask(downloadTaskModel)
        downloadTaskModelList.remove(downloadTaskModel)
        DBUtils.deleteDownloadTask(downloadTaskModel)
    }

    /**
     * Key of map is "yyyy-MM-dd HH:mm:ss.SSS" which is defined in DateTimeUtils.kt
     * Value of map is a instance of DownloadTask
     */
    private fun saveTaskToDB(downloadTask: DownloadTaskModel) {
        val taskID = DateTimeUtils.time2String(downloadTask.createdAt)
        logger.debug("add models $taskID to download models db")
        DBUtils.saveDownloadTask(taskID, downloadTask)
    }

    private fun addTaskToList(downloadTask: DownloadTaskModel) {
        downloadTaskModelList.add(downloadTask)
        startTask(downloadTask)
    }

    fun loadTaskFromDB() {
        DBUtils.loadAllDownloadTasks().forEach {
            downloadTaskModelList.add(it)
        }

        downloadTaskModelList.sortBy {
            it.createdAt
        }
    }

    fun clear() {
        stopAllTask()
        // TODO refresh in db
    }
}
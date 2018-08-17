package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engines.AbstractEngine
import com.ingbyr.vdm.engines.utils.EngineFactory
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.events.UpdateEngineTask
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskStatus
import com.ingbyr.vdm.models.DownloadTaskType
import com.ingbyr.vdm.models.TaskConfig
import com.ingbyr.vdm.utils.DBUtils
import com.ingbyr.vdm.utils.DateTimeUtils
import com.ingbyr.vdm.utils.NetUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class MainController : Controller() {

    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    val downloadTaskModelList = mutableListOf<DownloadTaskModel>().observable()
    private val engineList = ConcurrentHashMap<String, AbstractEngine>() // FIXME auto clean the finished models engines

    init {
        subscribe<CreateDownloadTask> {
            logger.debug("create models: ${it.downloadTask}")
            addTaskToList(it.downloadTask)
            saveTaskToDB(it.downloadTask)
        }

        // background thread
        subscribe<UpdateEngineTask> {
            val engine = EngineFactory.create(it.engineType)
//            val vdmConfig = TaskConfig(it.engineType, VDMProxy(ProxyType.NONE), false, engine!!.enginePath)
            val taskConfig = TaskConfig("", it.engineType, DownloadTaskType.ENGINE, true, engine.enginePath)
//            val downloadTask = DownloadTaskModel(vdmConfig, "", LocalDateTime.now(), title = "[${messages["ui.update"]} ${it.engineType.name}] ", type = DownloadTaskType.ENGINE)
            val downloadTask = DownloadTaskModel(taskConfig, DateTimeUtils.now(), title = "[${messages["ui.update"]} ${it.engineType.name}]")
            downloadTaskModelList.add(downloadTask)

            try {
                if (engine.existNewVersion(it.localVersion)) {
                    downloadTask.taskConfig.url = engine.updateUrl()
                    logger.info("update the ${downloadTask.taskConfig.engineType} from ${downloadTask.taskConfig.url}")
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
        if (downloadTask.taskConfig.downloadType == DownloadTaskType.ENGINE) return
        downloadTask.status = DownloadTaskStatus.ANALYZING
        runAsync {
            // download
            val engine = EngineFactory.create(downloadTask.taskConfig.engineType)
            engineList[downloadTask.createdAt] = engine
            val taskConfig = downloadTask.taskConfig
            engine.url(taskConfig.url)
                    .addProxy(taskConfig.proxyType, taskConfig.proxyAddress, taskConfig.proxyPort)
                    .format(taskConfig.formatId)
                    .output(taskConfig.storagePath)
                    .cookies(taskConfig.cookie)
                    .ffmpegPath(taskConfig.ffmpeg)
                    .downloadMedia(downloadTask, messages)
        }
    }

    fun startAllTask() {
        downloadTaskModelList.forEach {
            if (it.status != DownloadTaskStatus.COMPLETED && it.taskConfig.downloadType != DownloadTaskType.ENGINE) {
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

    private fun saveTaskToDB(downloadTask: DownloadTaskModel) {
        logger.debug("add download task $downloadTask to db")
        DBUtils.saveDownloadTask(downloadTask)
    }

    private fun addTaskToList(downloadTask: DownloadTaskModel) {
        downloadTaskModelList.add(downloadTask)
        // startTask(downloadTask)  // TODO debug. uncomment this
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
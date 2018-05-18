package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.engine.EngineFactory
import com.ingbyr.vdm.models.DownloadTaskData
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.utils.DateTimeUtils
import com.ingbyr.vdm.utils.VDMContent
import org.mapdb.DBMaker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*


class MainController : Controller() {

    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val db = DBMaker.fileDB(VDMContent.DATABASE_PATH_STR).transactionEnable().make()
    val downloadTaskData = db.hashMap(VDMContent.DB_DOWNLOAD_TASKS).createOrOpen() as MutableMap<String, DownloadTaskData>

    fun startDownloadTask(downloadTask: DownloadTaskModel) {
        runAsync {
            val engine = EngineFactory.create(downloadTask.vdmConfig.engineType)
            engine?.run {
                this.url(downloadTask.url).addProxy(downloadTask.vdmConfig.proxy).format(downloadTask.formatID).output(downloadTask.vdmConfig.storagePath).downloadMedia(downloadTask, messages)
            }
        }
    }

    /**
     * Key of map is "yyyy-MM-dd HH:mm:ss.SSS" which is defined in DateTimeUtils.kt
     * Value of map is a instance of DownloadTask (instance.createdAt's type is LocalDateTime)
     */
    fun saveTaskToDB(downloadTask: DownloadTaskData) {
        val taskID = DateTimeUtils.time2String(downloadTask.createdAt!!)
        logger.debug("add task $taskID to download task db")
        downloadTaskData[taskID] = downloadTask
    }

    fun updateGUI() {
        // TODO update VDM like as youtube-dl rules
        hostServices.showDocument(VDMContent.APP_UPDATE_URL)
    }

    fun clear() {
        db.commit()
        db.close()
    }
}
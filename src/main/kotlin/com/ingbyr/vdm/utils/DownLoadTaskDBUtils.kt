package com.ingbyr.vdm.utils

import com.ingbyr.vdm.models.DownloadTaskModel
import org.slf4j.LoggerFactory

object DownLoadTaskDBUtils {

    private val log = LoggerFactory.getLogger(DownLoadTaskDBUtils::class.java)
    private val dbUri = "jdbc:h2:~/.vdm/h2db"
    private val tableName = "DOWNLOAD_TASK"

    fun saveDownloadTask(downloadTask: DownloadTaskModel) {
        log.debug("save download models to db")
    }

    fun deleteDownloadTask(downloadTask: DownloadTaskModel) {
        log.debug("delete download models from db")
    }

    fun loadAllDownloadTasks(): List<DownloadTaskModel> {
        log.debug("load all download tasks")
        return listOf()
    }
}
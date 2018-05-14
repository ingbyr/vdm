package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.models.DownloadTask
import com.ingbyr.vdm.utils.VDMContent
import org.mapdb.DBMaker
import tornadofx.*
import java.util.*


class MainController : Controller() {
    private val db = DBMaker.fileDB(VDMContent.DATABASE_PATH_STR).transactionEnable().make()
    val downloadTaskData = db.treeSet(VDMContent.DB_DOWNLOAD_TASKS).createOrOpen() as NavigableSet<DownloadTask>

    fun updateGUI() {
        hostServices.showDocument(VDMContent.APP_UPDATE_URL)
    }

    fun clear() {
        db.close()
    }
}
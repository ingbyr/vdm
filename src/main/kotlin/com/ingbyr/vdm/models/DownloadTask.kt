package com.ingbyr.vdm.models

import com.ingbyr.vdm.utils.VDMConfig
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.Serializable

/**
 * No need to save proxy settings in task instance
 */
class DownloadTaskModel(data: DownloadTask) : ViewModel() {
    val checkedProperty = SimpleBooleanProperty(this, "checked", data.checked ?: false)
    val titleProperty = SimpleStringProperty(this, "title", data.title ?: "No title")
    val sizeProperty = SimpleStringProperty(this, "size", data.size ?: "No size")
    val progressProperty = SimpleDoubleProperty(this, "progress", data.progress ?: 0.0)
}

data class DownloadTask(val vdmConfig: VDMConfig, val url: String, var formatID: String = "", var checked: Boolean? = null, var title: String? = null, var size: String? = null, var progress: Double? = null) : Serializable

//fun main(args: Array<String>) {
//    val db = DBMaker.fileDB(Paths.get(VDMContent.USER_DIR.toString(), "test.db").toString()).transactionEnable().make()
//    val tasksDB = db.treeSet("tasks").createOrOpen() as NavigableSet<DownloadTask>
//    val vdmConfig = VDMConfig(EngineType.YOUTUBE_DL, VDMProxy(ProxyType.NONE), false, "output")
//    val downloadTask = DownloadTask(vdmConfig, "test url", "22")
//    tasksDB.add(downloadTask)
//    db.commit()
//
//    val data = db.get<NavigableSet<DownloadTask>>("tasks")
//    println(data)
//    db.close()
//}
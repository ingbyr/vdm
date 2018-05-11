package com.ingbyr.vdm.models

import com.ingbyr.vdm.utils.VDMConfig
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

/**
 * No need to save proxy settings in task instance
 */
class DownloadTask(val taskConfig: DownloadTaskConfig, checked: Boolean? = null, title: String? = null, size: String? = null, progress: Double? = null) : ViewModel() {
    val checkedProperty = SimpleBooleanProperty(this, "checked", checked ?: false)
    val titleProperty = SimpleStringProperty(this, "title", title ?: "No title")
    val sizeProperty = SimpleStringProperty(this, "size", size ?: "No size")
    val progressProperty = SimpleDoubleProperty(this, "progress", progress ?: 0.0)
}

data class DownloadTaskConfig(val url: String, val outputPath: String, var formatID: String, val vdmConfig: VDMConfig)
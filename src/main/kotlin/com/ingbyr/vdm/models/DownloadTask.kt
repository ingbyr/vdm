package com.ingbyr.vdm.models

import com.ingbyr.vdm.utils.EngineType
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

/**
 * No need to save proxy settings in task instance
 */
class DownloadTask(checked: Boolean? = null, title: String? = null, size: String? = null, progress: Double? = null, val downloadTaskConfig: DownloadTaskConfig) : ViewModel() {
    val checkedProperty = SimpleBooleanProperty(this, "checked", checked ?: false)
    val titleProperty = SimpleStringProperty(this, "title", title ?: "No title")
    val sizeProperty = SimpleStringProperty(this, "size", size ?: "No ")
    val progressProperty = SimpleDoubleProperty(this, "progress", progress ?: 0.0)
}

data class DownloadTaskConfig(val engineType: EngineType, val url: String, var outputPath: String, var formatID: String = "")

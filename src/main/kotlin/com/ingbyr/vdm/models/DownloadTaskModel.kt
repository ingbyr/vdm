package com.ingbyr.vdm.models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.LocalDateTime
import java.util.*

class DownloadTaskModel(
        val taskConfig: TaskConfig,
        createdAt: String? = null,
        checked: Boolean? = false,
        title: String? = null,
        size: String? = null,
        progress: Double? = 0.0,
        status: DownloadTaskStatus? = null) : ViewModel() {

    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    val checkedProperty = SimpleBooleanProperty(this, "checked", checked ?: false)
    var checked: Boolean by checkedProperty
    val titleProperty = SimpleStringProperty(this, "title", title ?: messages["ui.analyzing"])
    var title: String by titleProperty
    val sizeProperty = SimpleStringProperty(this, "size", size ?: messages["ui.analyzing"])
    var size: String by sizeProperty
    val statusProperty = SimpleObjectProperty<DownloadTaskStatus>(this, "status", status
            ?: DownloadTaskStatus.ANALYZING)
    var status: DownloadTaskStatus by statusProperty
    val progressProperty = SimpleDoubleProperty(this, "progress", progress ?: 0.0)
    var progress: Double by progressProperty
    val createdAtProperty = SimpleStringProperty(this, "createdAt", createdAt)
    var createdAt: String by createdAtProperty
}
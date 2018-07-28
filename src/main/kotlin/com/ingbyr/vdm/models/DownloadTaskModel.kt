package com.ingbyr.vdm.models

import com.ingbyr.vdm.utils.VDMConfig
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.LocalDateTime
import java.util.*

class DownloadTaskModel(
        val vdmConfig: VDMConfig,
        var url: String, createdAt: LocalDateTime? = null,
        formatID: String? = null, checked: Boolean? = null,
        title: String? = null, size: String? = null,
        progress: Double? = null,
        status: DownloadTaskStatus? = null,
        type: DownloadTaskType? = null) : ViewModel() {

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
    val createdAtProperty = SimpleObjectProperty<LocalDateTime>(this, "createdAt", createdAt)
    var createdAt: LocalDateTime by createdAtProperty
    var formatID = formatID ?: ""
    var typeProperty = SimpleObjectProperty<DownloadTaskType>(this, "type", type)
    var type: DownloadTaskType by typeProperty
}
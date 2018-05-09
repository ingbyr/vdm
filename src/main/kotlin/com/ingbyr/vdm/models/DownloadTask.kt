package com.ingbyr.vdm.models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel

class DownloadTask(checked: Boolean? = null, title: String? = null, size: String? = null, progress: Double? = null) : ViewModel() {
    val checkedProperty = SimpleBooleanProperty(this, "checked", checked ?: false)
    val titleProperty = SimpleStringProperty(this, "title", title ?: "No title")
    val sizeProperty = SimpleStringProperty(this, "size", size ?: "No ")
    val progressProperty = SimpleDoubleProperty(this, "progress", progress ?: 0.0)
}

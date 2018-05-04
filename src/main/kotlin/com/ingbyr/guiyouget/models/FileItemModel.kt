package com.ingbyr.guiyouget.models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel

class FileItem(checked: Boolean? = null, title: String? = null, size: String? = null, progress: Double? = null) {
    val checkedProperty = SimpleBooleanProperty(this, "checked", checked ?: false)
    val titleProperty = SimpleStringProperty(this, "title", title ?: "No title")
    val sizeProperty = SimpleStringProperty(this, "size", size ?: "No ")
    val progressProperty = SimpleDoubleProperty(this, "progress", progress ?: 0.0)
}

class FileItemModel : ItemViewModel<FileItem>() {
    val checked = bind(FileItem::checkedProperty)
    val title = bind(FileItem::titleProperty)
    val size = bind(FileItem::sizeProperty)
    val progress = bind(FileItem::progressProperty)
}
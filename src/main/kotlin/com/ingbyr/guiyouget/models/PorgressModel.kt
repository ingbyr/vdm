package com.ingbyr.guiyouget.models


import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Progress(progress: Double?, speed: String?, extTime: String?, status: String?) {
    val progressProperty = SimpleDoubleProperty(this, "progress", progress ?: 0.0)
    var progress by progressProperty
    val speedProperty = SimpleStringProperty(this, "speed", speed)
    var speed by speedProperty
    val extTimeProperty = SimpleStringProperty(this, "extTime", extTime)
    var extTime by extTimeProperty
    val statusProperty = SimpleStringProperty(this, "status", status)
    var status by statusProperty
}

class ProgressModel(var pg: Progress) : ViewModel() {
    val progress = bind { pg.progressProperty }
    val speed = bind { pg.speedProperty }
    val extTime = bind { pg.extTimeProperty }
    val status = bind { pg.statusProperty }
}
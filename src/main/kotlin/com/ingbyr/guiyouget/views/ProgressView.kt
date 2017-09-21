package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.ProgressController
import com.ingbyr.guiyouget.events.DownloadMediaRequest
import com.ingbyr.guiyouget.models.Progress
import com.ingbyr.guiyouget.models.ProgressModel
import com.jfoenix.controls.JFXProgressBar
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import tornadofx.*


class ProgressView : View() {
    private val pg = Progress(0.0, "0MiB/s", "00:00", "Analyzing...")
    private val model = ProgressModel(pg)
    private val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")

    private val progressbar: JFXProgressBar by fxid()
    private val paneExit: Pane by fxid()
    private val labelTitle: Label by fxid()
    private val labelSpeed: Label by fxid()
    private val labelTime: Label by fxid()

    init {
        paneExit.setOnMouseClicked {
            this.close()
        }

        progressbar.progressProperty().bind(model.progress)
        labelTime.textProperty().bind(model.extTime)
        labelSpeed.textProperty().bind(model.speed)
        labelTitle.textProperty().bind(model.status)

        // Subscribe Events
        subscribe<DownloadMediaRequest> {
            controller.download(pg, it)
        }
    }
}

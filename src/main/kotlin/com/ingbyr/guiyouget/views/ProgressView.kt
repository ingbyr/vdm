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
    val pg = Progress(0.0, "0MiB/s", "00:00", "Analyzing...")
    val model = ProgressModel(pg)
    val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")

    val progressbar: JFXProgressBar by fxid()
    val paneExit: Pane by fxid()
    val labelTitle: Label by fxid()
    val labelSpeed: Label by fxid()
    val labelTime: Label by fxid()

    init {
        paneExit.setOnMouseClicked {
            this.close()
        }

//        subscribe<UpdateMediaProgressbar> {
//            progressbar.progress = it.progress / 100
//            labelProgress.text = "${it.progress}%"
//            labelSpeed.text = it.speed ?: "0MiB/s"
//            labelTime.text = it.extTime ?: "00:00"
//        }

        progressbar.progressProperty().bind(model.progress)
        labelTime.textProperty().bind(model.extTime)
        labelSpeed.textProperty().bind(model.speed)
        labelTitle.textProperty().bind(model.status)

        subscribe<DownloadMediaRequest> {
            controller.download(pg, it)
        }
    }
}

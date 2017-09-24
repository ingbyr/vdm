package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.ProgressController
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYouGet
import com.ingbyr.guiyouget.events.UpdateProgressWithYoutubeDL
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXProgressBar
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import tornadofx.*


class ProgressView : View() {
    private val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")

    private val progressbar: JFXProgressBar by fxid()
    private val paneExit: Pane by fxid()
    private val labelTitle: Label by fxid()
    private val labelSpeed: Label by fxid()
    private val labelTime: Label by fxid()
    private val btnCancel: JFXButton by fxid()

    init {
        controller.subscribeEvents()

        paneExit.setOnMouseClicked {
            fire(StopDownloading)
            this.close()
        }

        btnCancel.setOnMouseClicked {
            fire(StopDownloading)
        }

        subscribe<UpdateProgressWithYoutubeDL> {
            labelTime.text = it.extime
            labelSpeed.text = it.speed
            labelTitle.text = it.status
            progressbar.progress = it.progress / 100
        }

        subscribe<UpdateProgressWithYouGet> {
            labelSpeed.text = it.speed
            labelTitle.text = it.status
            progressbar.progress = it.progress / 100
        }

    }
}

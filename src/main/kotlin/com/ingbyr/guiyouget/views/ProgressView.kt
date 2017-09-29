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
import java.util.*


class ProgressView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/core")
    }

    private val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")

    private val progressbar: JFXProgressBar by fxid()
    private val paneExit: Pane by fxid()
    private val labelTitle: Label by fxid()
    private val labelSpeed: Label by fxid()
    private val labelTime: Label by fxid()
    private val btnPause: JFXButton by fxid()
    private val btnResume: JFXButton by fxid()

    init {
        controller.subscribeEvents()

        paneExit.setOnMouseClicked {
            fire(StopDownloading)
            this.close()
        }

        //todo 支持中断下载
        btnPause.setOnMouseClicked {
            fire(StopDownloading)
        }

        //todo 下载完成时自动关闭该页面，延迟关闭
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

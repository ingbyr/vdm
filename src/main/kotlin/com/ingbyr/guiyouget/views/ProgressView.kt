package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.ProgressController
import com.ingbyr.guiyouget.engine.ProxyType
import com.ingbyr.guiyouget.engine.YoutubeDLTest
import com.ingbyr.guiyouget.events.ResumeDownloading
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateProgressWithYouGet
import com.ingbyr.guiyouget.events.UpdateProgressWithYoutubeDL
import com.jfoenix.controls.JFXProgressBar
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*


class ProgressView : View() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        messages = ResourceBundle.getBundle("i18n/engine")
    }

    private val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")

    private val progressbar: JFXProgressBar by fxid()
    private val paneExit: Pane by fxid()
    private val labelTitle: Label by fxid()
    private val labelSpeed: Label by fxid()
    private val labelTime: Label by fxid()
    private val panePause: Pane by fxid()
    private val paneResume: Pane by fxid()

    init {
//        controller.subscribeEvents()

        paneExit.setOnMouseClicked {
            fire(StopDownloading)
            this.close()
        }

        panePause.isVisible = true
        paneResume.isVisible = false

        paneResume.setOnMouseClicked {
            paneResume.isVisible = false
            panePause.isVisible = true
            labelTitle.text = messages["resume"]
            fire(ResumeDownloading)
        }

        panePause.setOnMouseClicked {
            paneResume.isVisible = true
            panePause.isVisible = false
            labelTitle.text = messages["pause"]
            fire(StopDownloading)
        }

        // todo update ui in download thread
        runAsync {
            val ydt = YoutubeDLTest("test url")
            ydt.addProxy(ProxyType.SOCKS5, "127.0.0.1", "1080")
            val json = ydt.fetchMediaJson()
            json.string("title")
        } ui {
            labelTitle.text = it
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

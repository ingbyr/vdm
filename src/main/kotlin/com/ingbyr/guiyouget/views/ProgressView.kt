package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.ProgressController
import com.ingbyr.guiyouget.events.ResumeDownloading
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.jfoenix.controls.JFXProgressBar
import javafx.animation.AnimationTimer
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


class ProgressView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/engine")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")
    private val progressbar: JFXProgressBar by fxid()
    private val paneExit: Pane by fxid()
    private val labelTitle: Label by fxid()
    private val labelSpeed: Label by fxid()
    private val labelTime: Label by fxid()
    private val panePause: Pane by fxid()
    private val paneResume: Pane by fxid()

    private val url = params["url"] as String
    private val formatID = params["formatID"] as String
    private val msgQueue = ArrayBlockingQueue<Map<String, Any>>(1)

    init {
        paneExit.setOnMouseClicked {
            fire(StopBackgroundTask)
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
            fire(StopBackgroundTask)
        }


        runAsync {
            controller.download(url, formatID, msgQueue)
        }

        val lastUpdate = SimpleLongProperty()
        val minUpdateInterval: Long = 0
        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    val msg = msgQueue.poll()
                    if (msg != null) {
                        logger.debug(msg.toString())
                    }
                    lastUpdate.set(now)
                }
            }
        }
        timer.start()
    }
}

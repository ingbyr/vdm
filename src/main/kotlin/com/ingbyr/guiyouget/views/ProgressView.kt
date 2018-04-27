package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.ProgressController
import com.ingbyr.guiyouget.events.DownloadMedia
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.models.CurrentConfig
import com.ingbyr.guiyouget.utils.EngineStatus
import com.jfoenix.controls.JFXProgressBar
import javafx.animation.AnimationTimer
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import org.slf4j.LoggerFactory
import tornadofx.View
import tornadofx.get
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


class ProgressView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/ProgressView")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")
    private val progressbar: JFXProgressBar by fxid()
    private val paneExit: Pane by fxid()
    private val paneMinimize: Pane by fxid()
    private val labelTitle: Label by fxid()
    private val labelSpeed: Label by fxid()
    private val labelTime: Label by fxid()
    private val panePause: Pane by fxid()
    private val paneResume: Pane by fxid()
    private val apBorder: AnchorPane by fxid()
    private var xOffset = 0.0
    private var yOffset = 0.0
    private var ccf: CurrentConfig? = null
    private val msgQueue = ConcurrentLinkedQueue<Map<String, Any>>()

    init {
        apBorder.setOnMousePressed { event: MouseEvent? ->
            event?.let {
                xOffset = it.sceneX
                yOffset = it.sceneY
            }
        }

        apBorder.setOnMouseDragged { event: MouseEvent? ->
            event?.let {
                this.currentStage?.x = it.screenX - xOffset
                this.currentStage?.y = it.screenY - yOffset
            }
        }

        paneMinimize.setOnMouseClicked {
            primaryStage.isIconified = true
        }

        paneExit.setOnMouseClicked {
            fire(StopBackgroundTask)
            this.close()
        }

        panePause.isVisible = true
        paneResume.isVisible = false

        paneResume.setOnMouseClicked {
            logger.debug("resume the download task")
            paneResume.isVisible = false
            panePause.isVisible = true
            labelTitle.text = messages["resume"]
            runAsync {
                controller.download(ccf!!, msgQueue)
            }
        }

        panePause.setOnMouseClicked {
            logger.debug("pause the download task")
            paneResume.isVisible = true
            panePause.isVisible = false
            labelTitle.text = messages["pause"]
            fire(StopBackgroundTask)
        }


        subscribe<DownloadMedia> {
            ccf = it.ccf
            runAsync {
                controller.download(ccf!!, msgQueue)
            }
        }

        // update display info
        val lastUpdate = SimpleLongProperty()
        val minUpdateInterval: Long = 0
        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    val msg = msgQueue.poll()
                    msg?.let {
                        when (EngineStatus.valueOf(it["status"].toString())) {
                            EngineStatus.ANALYZE -> {
                                labelTitle.text = messages["analyzing"]
                            }

                            EngineStatus.DOWNLOAD -> {
                                labelTitle.text = messages["downloading"]
                            }

                            EngineStatus.FAIL -> {
                                find<TipView>(mapOf("title" to messages["failed"], "content" to "", "btnText" to messages["btn.close"])).openWindow(StageStyle.UNDECORATED)
                                this@ProgressView.close()
                            }

                            EngineStatus.FINISH -> {
                                find<TipView>(mapOf("title" to messages["completed"], "content" to "", "btnText" to messages["btn.close"])).openWindow(StageStyle.UNDECORATED)
                                this@ProgressView.close()
                            }

                            EngineStatus.PAUSE -> {
                                labelTitle.text = messages["pause"]
                            }

                            EngineStatus.RESUME -> {
                                labelTitle.text = messages["resume"]
                            }
                        }
                        labelSpeed.text = it["speed"] as String
                        labelTime.text = it["extime"] as String
                        progressbar.progress = it["progress"] as Double
                    }
                    lastUpdate.set(now)
                }
            }
        }
        timer.start()
    }
}

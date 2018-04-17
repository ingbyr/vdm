package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.ProgressController
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.utils.EngineStatus
import com.ingbyr.guiyouget.utils.EngineType
import com.ingbyr.guiyouget.utils.ProxyType
import com.jfoenix.controls.JFXProgressBar
import javafx.animation.AnimationTimer
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque


class ProgressView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/engine")
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

    private val url = params["url"] as String
    private val formatID = params["formatID"] as String
    private val msgQueue = ConcurrentLinkedDeque<Map<String, Any>>()
    private var proxyType: ProxyType = params["proxyType"] as ProxyType
    private var address: String = params["address"] as String
    private var port: String = params["port"] as String
    private var engineType: EngineType = params["engineType"] as EngineType

    init {
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
                controller.download(engineType, url, proxyType, address, port, formatID, msgQueue)
            }
        }

        panePause.setOnMouseClicked {
            logger.debug("pause the download task")
            paneResume.isVisible = true
            panePause.isVisible = false
            labelTitle.text = messages["pause"]
            fire(StopBackgroundTask)
        }

        // start download task in background
        runAsync {
            controller.download(engineType, url, proxyType, address, port, formatID, msgQueue)
        }

        val lastUpdate = SimpleLongProperty()
        val minUpdateInterval: Long = 0
        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    val msg = msgQueue.poll()
                    msg?.let {
                        labelTitle.text = translateStatus(it["status"] as EngineStatus)
                        labelSpeed.text = it["speed"] as String
                        labelTime.text = it["extime"] as String
                        progressbar.progress = it["progress"] as Double

                        if (it["status"] as EngineStatus == EngineStatus.FINISH) {
                            // todo fire event to reset main UI
                            this@ProgressView.close()
                        }
                    }
                    lastUpdate.set(now)
                }
            }
        }
        timer.start()
    }

    private fun translateStatus(type: EngineStatus): String {
        return when (type) {
            EngineStatus.ANALYZE -> {
                messages["analyzing"]
            }
            EngineStatus.DOWNLOAD -> {
                messages["downloading"]
            }
            EngineStatus.FAIL -> {
                messages["failed"]
            }
            EngineStatus.FINISH -> {
                messages["completed"]
            }
            EngineStatus.PAUSE -> {
                messages["pause"]
            }
            EngineStatus.RESUME -> {
                messages["resume"]
            }
        }
    }
}

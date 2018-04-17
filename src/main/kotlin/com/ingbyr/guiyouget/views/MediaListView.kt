package com.ingbyr.guiyouget.views

import com.beust.klaxon.JsonObject
import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.utils.EngineType
import com.ingbyr.guiyouget.utils.ProxyType
import com.jfoenix.controls.JFXListView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class MediaListView : View("GUI-YouGet") {

    init {
        messages = ResourceBundle.getBundle("i18n/MediaListView")
    }

    override val root: AnchorPane by fxml("/fxml/MediaListWindow.fxml")
    private val logger = LoggerFactory.getLogger(MediaListView::class.java)

    private var xOffset = 0.0
    private var yOffset = 0.0

    private val paneExit: Pane by fxid()
    private val paneMinimize: Pane by fxid()
    private val paneBack: Pane by fxid()
    private val apBorder: AnchorPane by fxid()

    private val controller: MediaListController by inject()

    private val labelTitle: Label by fxid()
    private val labelDescription: Label by fxid()
    private val listViewMedia: JFXListView<Label> by fxid()

    // args from main view config
    private var url: String? = null
    private var proxyType: ProxyType? = null
    private var address: String? = null
    private var port: String? = null
    private var engineType: EngineType? = null

    init {
        // Window boarder
        apBorder.setOnMousePressed { event: MouseEvent? ->
            event?.let {
                xOffset = event.sceneX
                yOffset = event.sceneY
            }
        }

        apBorder.setOnMouseDragged { event: MouseEvent? ->
            event?.let {
                primaryStage.x = event.screenX - xOffset
                primaryStage.y = event.screenY - yOffset
            }
        }

        paneExit.setOnMouseClicked {
            Platform.exit()
        }

        paneMinimize.setOnMouseClicked {
            primaryStage.isIconified = true
        }

        paneBack.setOnMouseClicked {
            replaceWith(MainView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
        }

        listViewMedia.setOnMouseClicked {
            listViewMedia.selectedItem?.let {
                if (url != null && proxyType != null && address != null && port != null && engineType != null) {
                    val formatID = it.text.split(" ")[0]
                    logger.debug("start download ${it.text}, format id is $formatID")
                    url?.let {
                        // if url is not null, display progress view to download
                        find<ProgressView>(mapOf("url" to url, "formatID" to formatID, "proxyType" to proxyType, "address" to address, "port" to port, "engineType" to engineType)).openModal(StageStyle.UNDECORATED)
                    }
                } else {
                    logger.error("download engine args error: url | proxyType | address | port | engineType ")
                    logger.error("$url | $proxyType | $address | $port | $engineType ")
                }
            }
        }
    }

    private fun displayMedia() {
        // load the args from main view config
        url = params["url"] as? String
        proxyType = params["proxyType"] as? ProxyType
        address = params["address"] as? String
        port = params["port"] as? String
        engineType = params["engineType"] as? EngineType

        // fetch media json and display it
        if (url != null && proxyType != null && address != null && port != null) {
            runAsync {
                controller.requestMedia(engineType!!, url!!, proxyType!!, address!!, port!!)
            } ui {
                if (it != null) {
                    controller.displayMedia(engineType!!, labelTitle, labelDescription, listViewMedia, it)
                } else {
                    labelTitle.text = messages["failed"]
                }
            }
        } else {
            //todo bad url. need display the tip
        }
    }

    private fun resetUI() {
        labelTitle.text = messages["label.loading"]
        labelDescription.text = ""
        listViewMedia.items.clear()
    }

    override fun onUndock() {
        /**
         * Reset UI and clean the background task
         */
        resetUI()
        // clean the thread
        fire(StopBackgroundTask)
    }

    override fun onDock() {
        resetUI()
        displayMedia() // fetch media json and display
    }
}

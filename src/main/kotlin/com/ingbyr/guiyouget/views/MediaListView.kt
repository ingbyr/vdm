package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.events.DownloadMedia
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.models.CurrentConfig
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

class MediaListView : View() {

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
    private val labelTitle: Label by fxid()
    private val labelDescription: Label by fxid()
    private val listViewMedia: JFXListView<Label> by fxid()
    private val controller: MediaListController by inject()
    private var ccf = params["ccf"] as CurrentConfig

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
                val formatID = it.text.split(" ")[0]
                logger.debug("start download ${it.text}, format id is $formatID")
                ccf.formatID = formatID
                ProgressView().openWindow(StageStyle.UNDECORATED)
                fire(DownloadMedia(ccf))
            }
        }
    }

    private fun displayMedia() {
        // fetch media json and display it
        runAsync {
            controller.requestMedia(ccf.engineType, ccf.url, ccf.proxyType, ccf.address, ccf.port)
        } ui {
            if (it != null) {
                controller.engine?.displayMediaList(labelTitle, labelDescription, listViewMedia, it)
            } else {
                labelTitle.text = messages["failed"]
            }
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
        ccf = params["ccf"] as CurrentConfig
        resetUI()
        displayMedia() // fetch media json and display
    }
}

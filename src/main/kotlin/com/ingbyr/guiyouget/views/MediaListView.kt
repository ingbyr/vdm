package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.utils.EngineUtils
import com.jfoenix.controls.JFXListView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class MediaListView : View("GUI-YouGet") {

    init {
        messages = ResourceBundle.getBundle("i18n/MediaListView")
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(MediaListView::class.java)
    }

    override val root: AnchorPane by fxml("/fxml/MediaListWindow.fxml")

    private var xOffset = 0.0
    private var yOffset = 0.0
//    private var url = params["url"] as? String

    private val paneExit: Pane by fxid()
    private val paneBack: Pane by fxid()
    private val apBorder: AnchorPane by fxid()

    private val controller: MediaListController by inject()

    private val labelTitle: Label by fxid()
    private val labelDescription: Label by fxid()
    private val listViewMedia: JFXListView<Label> by fxid()

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

        paneBack.setOnMouseClicked {
            replaceWith(MainView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
        }

        listViewMedia.setOnMouseClicked {
            listViewMedia.selectedItem?.let {
                logger.debug("select ${it.text}")
                val formatID = it.text.split(" ")[0]
                ProgressView().openModal(StageStyle.UNDECORATED)
                // todo download in progress view. need pass the format ID
            }
        }
    }

    private fun displayMedia() {
        val url = params["url"] as? String  // update url when docked
        // fetch media json and display it
        if (url != null) {
            runAsync {
                controller.requestMedia(url)
            } ui {
                when (app.config.string(EngineUtils.DOWNLOAD_CORE)) {
                    EngineUtils.YOUTUBE_DL -> {
                        labelTitle.text = it.string("title")
                        labelDescription.text = it.string("description") ?: ""
                        controller.addMediaItemsYoutubeDL(listViewMedia, it.array("formats"))
                    }
                    EngineUtils.YOU_GET -> {
                        labelTitle.text = it.string("title")
                        labelDescription.text = ""
                        controller.addMediaItemsYouGet(listViewMedia, it.array("streams"))
                    }
                }
            }
        } else {
            //todo bad url. need display the tip
        }
    }

    override fun onUndock() {
        /**
         * Reset UI and clean the background task
         */
        listViewMedia.items.clear()
        labelTitle.text = messages["label.loading"]
        labelDescription.text = ""
        // clean the thread
        fire(StopDownloading)
    }

    override fun onDock() {
        displayMedia()
    }
}

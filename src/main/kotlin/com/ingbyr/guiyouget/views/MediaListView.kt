package com.ingbyr.guiyouget.views

import com.beust.klaxon.array
import com.beust.klaxon.string
import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.engine.YouGet
import com.ingbyr.guiyouget.engine.YoutubeDL
import com.ingbyr.guiyouget.events.DisplayMediasWithYouGet
import com.ingbyr.guiyouget.events.DisplayMediasWithYoutubeDL
import com.ingbyr.guiyouget.events.DownloadingRequestWithYouGet
import com.ingbyr.guiyouget.events.DownloadingRequestWithYoutubeDL
import com.ingbyr.guiyouget.utils.ContentsUtil
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
    private lateinit var url: String

    private val paneExit: Pane by fxid()
    private val paneBack: Pane by fxid()
    private val apBorder: AnchorPane by fxid()

    private val controller: MediaListController by inject()

    private val labelTitle: Label by fxid()
    private val labelDescription: Label by fxid()
    private val listViewMedia: JFXListView<Label> by fxid()


    init {
        controller.subscribeEvents()
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
                logger.debug("select format id is ${formatID}")
                ProgressView().openModal(StageStyle.UNDECORATED)
                when (app.config[ContentsUtil.DOWNLOAD_CORE]) {
                    ContentsUtil.YOUTUBE_DL -> fire(DownloadingRequestWithYoutubeDL(YoutubeDL(url), formatID))
                    ContentsUtil.YOU_GET -> fire(DownloadingRequestWithYouGet(YouGet(url), formatID))
                }
            }
        }

        // Subscribe Events
        subscribe<DisplayMediasWithYoutubeDL> {
            url = it.mediaList.string("webpage_url") ?: ""
            labelTitle.text = it.mediaList.string("title")
            labelDescription.text = it.mediaList.string("description")
            controller.addMediaItemsYoutubeDL(listViewMedia, it.mediaList.array("formats"))
        }

        subscribe<DisplayMediasWithYouGet> {
            url = it.mediaList.string("url") ?: ""
            labelTitle.text = it.mediaList.string("title")
            labelDescription.text = ""
            controller.addMediaItemsYouGet(listViewMedia, it.mediaList["streams"])
        }
    }

    // reset the ui
    override fun onUndock() {
        listViewMedia.items.clear()
        labelTitle.text = messages["label.loading"]
        labelDescription.text = ""
    }
}

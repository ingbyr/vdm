package com.ingbyr.guiyouget.views

import com.beust.klaxon.array
import com.beust.klaxon.string
import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.events.DownloadMediaRequest
import com.ingbyr.guiyouget.events.MediaListEvent
import com.jfoenix.controls.JFXListView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import tornadofx.*

class MediaListView : View("GUI-YouGet") {

    override val root: AnchorPane by fxml("/fxml/MediaListWindow.fxml")

    var xOffset = 0.0
    var yOffset = 0.0
    lateinit var url: String

    val paneExit: Pane by fxid()
    val paneBack: Pane by fxid()
    val apBorder: AnchorPane by fxid()

    val controller: MediaListController by inject()

    val labelTitle: Label by fxid()
    val labelDescription: Label by fxid()
    val listViewMedia: JFXListView<Label> by fxid()


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

        //Subscribe Events
        subscribe<MediaListEvent> {
            url = it.mediaList.string("webpage_url") ?: ""
            labelTitle.text = it.mediaList.string("title")
            labelDescription.text = it.mediaList.string("description")
            controller.addMediaItems(listViewMedia, it.mediaList.array("formats"))
        }

        // list view的监听器
        listViewMedia.setOnMouseClicked {
            listViewMedia.selectedItem?.let {
                log.info("select ${it.text}")
                val formatID = it.text.split(" ")[0]
                log.info("select format id is ${formatID}")
                if (url != "") fire(DownloadMediaRequest(url, formatID))
                ProgressView().openModal(StageStyle.UNDECORATED)
            }
        }
    }

    // reset the ui
    override fun onUndock() {
        listViewMedia.items.clear()
        labelTitle.text = "Loading..."
        labelDescription.text = ""
    }
}

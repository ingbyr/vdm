package com.ingbyr.guiyouget.views

import com.beust.klaxon.*
import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.events.MediaListEvent
import com.ingbyr.guiyouget.models.Media
import com.jfoenix.controls.JFXListView
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import tornadofx.*

class MediaListWindow : View("My View") {

    override val root: AnchorPane by fxml("/fxml/MediaListWindow.fxml")

    var xOffset = 0.0
    var yOffset = 0.0

    val paneExit: Pane by fxid()
    val paneBack: Pane by fxid()
    val apBorder: AnchorPane by fxid()

    val controller = MediaListController()

    val labelTitle: Label by fxid()
    val labelDescription: Label by fxid()
    val listViewMedia: JFXListView<Label> by fxid()


    init {
        // Window boarder
        apBorder.setOnMousePressed { event: MouseEvent? ->
            xOffset = event!!.sceneX
            yOffset = event!!.sceneY
        }

        apBorder.setOnMouseDragged { event: MouseEvent? ->
            primaryStage.x = event!!.screenX - xOffset
            primaryStage.y = event!!.screenY - yOffset
        }

        paneExit.setOnMouseClicked {
            Platform.exit()
        }

        paneBack.setOnMouseClicked {
            replaceWith(MainView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
        }

        //Subscribe Events
        subscribe<MediaListEvent> {
            labelTitle.text = it.mediaList.string("title")
            labelDescription.text = it.mediaList.string("description")
            addMediaItems(it.mediaList.array<JsonObject>("formats"))
        }
    }

    private fun addMediaItems(formats: JsonArray<JsonObject>?) {
        if (formats != null) {
            val medias = mutableListOf<Media>().observable()
            formats.mapTo(medias) {
                Media(it.string("format"),
                        it.string("format_note"),
                        it.int("filesize"),
                        it.string("format_id"))
            }

            medias.forEach {
                listViewMedia.items.add(Label("${it.format}  ${it.size}MB"))
                //todo download args -f [format id]
            }

        }
    }
}

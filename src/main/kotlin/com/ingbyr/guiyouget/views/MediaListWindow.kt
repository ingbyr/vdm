package com.ingbyr.guiyouget.views

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.array
import com.beust.klaxon.string
import com.ingbyr.guiyouget.controllers.MediaListController
import com.ingbyr.guiyouget.events.MediaListEvent
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.*

class MediaListWindow() : View("My View") {
    override val root: VBox by fxml("/fxml/MediaListWindow.fxml")

    val controller = MediaListController()

    val labelTitle: Label by fxid()
    val labelDescription: Label by fxid()
    val listViewMedia: JFXListView<Label> by fxid()

    init {
        //Subscribe Events
        subscribe<MediaListEvent> {
            labelTitle.text = it.mediaList.string("title")
            labelDescription.text = it.mediaList.string("description")
            addMediaItems(it.mediaList.array<JsonObject>("formats"))
        }
    }

    private fun addMediaItems(formats: JsonArray<JsonObject>?) {
        if (formats != null) {
            for (format in formats) {
                listViewMedia.items.add(Label("${format.string("format_note")}"))
            }
        }
    }
}

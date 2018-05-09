package com.ingbyr.vdm.views

import com.ingbyr.vdm.models.MediaFormat
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.*
import java.util.*


class MediaFormatsListView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/MediaFormatsListView")
    }

    override val root: VBox by fxml("/fxml/MediaFormatsListView.fxml")

    private val labelTitle: Label by fxid()
    private val labelDesc: Label by fxid()
    private val listView: JFXListView<Label> by fxid()
    private val mediaFormatsList = mutableListOf<MediaFormat>().observable()

    init {

    }

}

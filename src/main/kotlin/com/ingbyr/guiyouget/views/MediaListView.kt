package com.ingbyr.guiyouget.views

import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import tornadofx.*
import java.util.*


class MediaListView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/MediaListView")
    }

    override val root: AnchorPane by fxml("/fxml/MediaListView.fxml")

    private val labelTitle: Label by fxid()
    private val labelDesc: Label by fxid()
    private val listView: JFXListView<Label> by fxid()
}

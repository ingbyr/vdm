package com.ingbyr.guiyouget.views

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import tornadofx.*
import java.util.*


class CreateDownloadTaskView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/CreateDownloadTaskView")
    }

    override val root: AnchorPane by fxml("/fxml/CreateDownloadTaskView.fxml")

    private val tfURL: JFXTextField by fxid()
    private val btnMoreSettings: JFXButton by fxid()
    private val btnConfirm: JFXButton by fxid()
    private val labelStoragePath: Label by fxid()
    private val btnChangeStoragePath: JFXButton by fxid()

    init {
        initListeners()
    }

    private fun initListeners() {
        btnMoreSettings.setOnMouseClicked {
            find(PreferencesView::class).openWindow()
        }

        btnConfirm.setOnMouseClicked {
            find(MediaListView::class).openWindow()
            close()
        }
    }
}
package com.ingbyr.vdm.views

import com.ingbyr.vdm.models.Media
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.*
import java.util.*


class CreateDownloadTaskView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/CreateDownloadTaskView")
    }

    override val root: VBox by fxml("/fxml/CreateDownloadTaskView.fxml")

    private val tfURL: JFXTextField by fxid()
    private val btnMoreSettings: JFXButton by fxid()
    private val btnConfirm: JFXButton by fxid()
    private val labelStoragePath: Label by fxid()
    private val btnChangeStoragePath: JFXButton by fxid()
    private val mediaList = mutableListOf<Media>(Media("22", "HD", 233, "test", "12min")).observable()

    init {
        initListeners()

    }

    private fun initListeners() {
        btnMoreSettings.setOnMouseClicked {
            find(PreferencesView::class).openWindow()
        }

        btnConfirm.setOnMouseClicked {
            replaceWith(component = MediaListView::class,
                    centerOnScreen = true,
                    sizeToScene = false,
                    transition = ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
        }
    }
}
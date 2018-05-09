package com.ingbyr.vdm.views

import com.ingbyr.vdm.models.DownloadTaskConfig
import com.ingbyr.vdm.utils.EngineType
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


    init {
        initListeners()
    }

    private fun initListeners() {
        btnMoreSettings.setOnMouseClicked {
            find(PreferencesView::class).openWindow()
        }

        btnConfirm.setOnMouseClicked {
            val downloadTaskConfig = DownloadTaskConfig(engineType = EngineType.YOUTUBE_DL, url = tfURL.text, formatID = "NONE", outputPath = "user dir")
            find<MediaFormatsListView>(mapOf("downloadTaskConfig" to downloadTaskConfig)).openWindow()
            this.close()
        }
    }
}
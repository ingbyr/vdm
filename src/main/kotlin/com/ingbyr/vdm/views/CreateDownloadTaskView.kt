package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.CreateDownloadTaskController
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.utils.Attributes
import com.ingbyr.vdm.utils.DateTimeUtils
import com.ingbyr.vdm.utils.config.update
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import tornadofx.*
import java.util.*


class CreateDownloadTaskView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/CreateDownloadTaskView")
        title = messages["ui.create"]
    }

    override val root: VBox by fxml("/fxml/CreateDownloadTaskView.fxml")

    private val tfURL: JFXTextField by fxid()
    private val btnMoreSettings: JFXButton by fxid()
    private val btnConfirm: JFXButton by fxid()
    private val labelStoragePath: Label by fxid()
    private val btnChangeStoragePath: JFXButton by fxid()

    private val controller: CreateDownloadTaskController by inject()

    init {
        loadVDMConfig()
        initListeners()
        addValidation()
    }

    private fun loadVDMConfig() {
        labelStoragePath.text = app.config.string(Attributes.STORAGE_PATH)
    }

    private fun initListeners() {
        btnMoreSettings.setOnMouseClicked {
            find(PreferencesView::class).openWindow()
        }

        btnConfirm.setOnMouseClicked {
            val downloadTask = controller.createDownloadTaskInstance(tfURL.text)
            if (controller.downloadDefaultFormat) {
                // create download task directly
                downloadTask.createdAt = DateTimeUtils.now()
                fire(CreateDownloadTask(downloadTask))
            } else {
                find<MediaFormatsView>(mapOf("downloadTask" to downloadTask)).openWindow()
            }
            this.close()
        }

        btnChangeStoragePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                val newPath = this.absoluteFile.toString()
                config.update(Attributes.STORAGE_PATH, newPath)
                labelStoragePath.text = newPath
            }
        }
    }

    private fun addValidation() {
        // validation for the url text field
        ValidationContext().addValidator(tfURL, tfURL.textProperty()) {
            if (!it!!.startsWith("http")) error(messages["inputCorrectURL"]) else null
        }
    }

    override fun onUndock() {
        super.onUndock()
        tfURL.text = ""
    }
}
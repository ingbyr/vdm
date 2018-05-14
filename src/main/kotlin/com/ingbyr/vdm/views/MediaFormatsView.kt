package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.MediaFormatsController
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.models.DownloadTask
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*


class MediaFormatsView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/MediaFormatsView")
    }

    override val root: VBox by fxml("/fxml/MediaFormatsView.fxml")
    private val controller: MediaFormatsController by inject()
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val labelTitle: Label by fxid()
    private val labelDesc: Label by fxid()
    private val listView: JFXListView<Label> by fxid()

    private val downloadTask = params["downloadTask"] as DownloadTask

    init {
        runAsync {
            controller.requestMedia(downloadTask.vdmConfig.engineType, downloadTask.url, downloadTask.vdmConfig.proxy.proxyType, downloadTask.vdmConfig.proxy.address, downloadTask.vdmConfig.proxy.port)
        } ui {
            if (it != null) {
                controller.engine?.displayMediaList(labelTitle, labelDesc, listView, it)
            } else {
                labelTitle.text = messages["failed"]
            }
        }
        initListeners()
    }

    private fun initListeners() {
        listView.setOnMouseClicked {
            listView.selectedItem?.let {
                val formatID = it.text.split(" ")[0]
                logger.debug("start download ${it.text}, format id is $formatID")
                downloadTask.formatID = formatID
                fire(CreateDownloadTask(downloadTask))
            }
        }
    }
}

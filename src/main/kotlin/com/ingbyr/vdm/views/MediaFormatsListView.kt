package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.MediaFormatsListViewController
import com.ingbyr.vdm.models.DownloadTaskConfig
import com.ingbyr.vdm.utils.VDMProxy
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*


class MediaFormatsListView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/MediaFormatsListView")
    }

    override val root: VBox by fxml("/fxml/MediaFormatsListView.fxml")
    private val controller: MediaFormatsListViewController by inject()
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val labelTitle: Label by fxid()
    private val labelDesc: Label by fxid()
    private val listView: JFXListView<Label> by fxid()

    private val dtc = params["dtc"] as DownloadTaskConfig
    private val vp = params["vp"] as VDMProxy

    init {
        runAsync {
            controller.requestMedia(dtc.engineType, dtc.url, vp.proxyType, vp.address, vp.port)
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
            }
        }
    }
}

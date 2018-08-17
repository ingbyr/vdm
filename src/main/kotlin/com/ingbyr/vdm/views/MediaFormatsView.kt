package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.MediaFormatsController
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.MediaFormat
import com.ingbyr.vdm.utils.DateTimeUtils
import com.jfoenix.controls.JFXListView
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.time.LocalDateTime
import java.util.*


class MediaFormatsView : View() {

    init {
        messages = ResourceBundle.getBundle("i18n/MediaFormatsView")
        title = messages["ui.mediaList"]
    }

    override val root: VBox by fxml("/fxml/MediaFormatsView.fxml")
    private val controller: MediaFormatsController by inject()
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val labelTitle: Label by fxid()
    private val labelDesc: Label by fxid()
    private val listView: JFXListView<Label> by fxid()

    // downloadTask's type must be DownloadTaskData
    private var downloadTask = params["downloadTask"] as DownloadTaskModel
    private var mediaFormatList: List<MediaFormat>? = null

    init {
        initListeners()
    }

    private fun initListeners() {
        listView.onUserSelect(1) {
            mediaFormatList?.get(listView.selectionModel.selectedIndex)?.let {
                logger.debug("format id: ${it.formatID}")
                downloadTask.taskConfig.formatId = it.formatID
                downloadTask.title = it.title
                downloadTask.size = "${it.fileSize / 1048576}MB"
                downloadTask.createdAt = DateTimeUtils.nowTimeString()
                fire(CreateDownloadTask(downloadTask))
                this.close()
            }
        }
    }

    private fun displayFormatList() {
        if (mediaFormatList != null && mediaFormatList!!.isNotEmpty()) {
            labelTitle.text = mediaFormatList!![0].title
            labelDesc.text = mediaFormatList!![0].desc

            mediaFormatList!!.forEach {
                listView.items.add(Label("${it.format} | ${it.ext} | ${it.fileSize / 1048576}MB"))
            }
        } else {
            labelTitle.text = messages["failed"]
        }
    }

    override fun onDock() {
        super.onDock()
        // get download models from create download models view
        downloadTask = params["downloadTask"] as DownloadTaskModel
        // request the media json based on download models in background thread
        runAsync {
            controller.requestMedia(downloadTask)
        } ui {
            mediaFormatList = it
            displayFormatList()
        }
    }

    override fun onUndock() {
        super.onUndock()
        controller.clear()
        listView.items.clear()
        labelTitle.text = messages["ui.loading"]
        labelDesc.text = ""
        mediaFormatList = null
    }
}

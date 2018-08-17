package com.ingbyr.vdm.views

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskType
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.models.TaskConfig
import com.ingbyr.vdm.utils.AppConfigUtils
import com.ingbyr.vdm.utils.AppProperties
import com.ingbyr.vdm.utils.DateTimeUtils
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

    private val cu = AppConfigUtils(app.config)

    init {
        // validation for the url text field
        ValidationContext().addValidator(tfURL, tfURL.textProperty()) {
            if (!it!!.startsWith("http")) error(messages["inputCorrectURL"]) else null
        }

        loadVDMConfig()
        initListeners()
    }

    private fun loadVDMConfig() {
        labelStoragePath.text = cu.load(AppProperties.STORAGE_PATH)
    }

    private fun initListeners() {
        btnMoreSettings.setOnMouseClicked {
            find(PreferencesView::class).openWindow()
        }

        btnConfirm.setOnMouseClicked {
            val engineType = EngineType.valueOf(cu.load(AppProperties.ENGINE_TYPE))
            val url = tfURL.text
            val storagePath = cu.load(AppProperties.STORAGE_PATH)
            val downloadDefaultFormat = cu.load(AppProperties.DOWNLOAD_DEFAULT_FORMAT).toBoolean()
            val ffmpeg = cu.load(AppProperties.FFMPEG_PATH)
            val cookie = "" // TODO support cookie

            val taskConfig = TaskConfig(
                    url, engineType, DownloadTaskType.SINGLE_MEDIA,
                    downloadDefaultFormat, storagePath, cookie, ffmpeg)

            val proxyType = ProxyType.valueOf(cu.load(AppProperties.PROXY_TYPE))
            when (proxyType) {
                ProxyType.SOCKS5 -> {
                    taskConfig.proxy(proxyType, cu.load(AppProperties.SOCKS5_PROXY_ADDRESS), cu.load(AppProperties.SOCKS5_PROXY_PORT))
                }
                ProxyType.HTTP -> {
                    taskConfig.proxy(proxyType, cu.load(AppProperties.SOCKS5_PROXY_ADDRESS), cu.load(AppProperties.SOCKS5_PROXY_PORT))
                }
                ProxyType.NONE -> {
                    taskConfig.proxy(proxyType, "", "")
                }
            }

            val downloadTask = DownloadTaskModel(taskConfig)

            if (downloadDefaultFormat) {
                // start download task directly
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
                app.config[AppProperties.STORAGE_PATH] = newPath
                labelStoragePath.text = newPath
                cu.saveToConfigFile()
            }
        }
    }

    override fun onUndock() {
        super.onUndock()
        tfURL.text = ""
    }
}
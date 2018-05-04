package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
import com.ingbyr.guiyouget.events.DownloadMedia
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYouGet
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYoutubeDL
import com.ingbyr.guiyouget.models.CurrentConfig
import com.ingbyr.guiyouget.utils.*
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXToggleButton
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.stage.StageStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.awt.Desktop
import java.lang.IllegalStateException
import java.nio.file.Paths
import java.util.*
import kotlin.collections.set


class MainView : View() {
    // TODO add download playlist function
    // TODO add download with cookie
    // TODO wizard to init config and engine env: ffmpeg

    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    private val logger: Logger = LoggerFactory.getLogger(MainView::class.java)
    private var xOffset = 0.0
    private var yOffset = 0.0
    override val root: AnchorPane by fxml("/fxml/MainWindow.fxml")
    private val controller: MainController by inject()

    private val paneExit: Pane by fxid()
    private val paneMinimize: Pane by fxid()
    private val apBorder: AnchorPane by fxid()
    private val tfURL: JFXTextField by fxid()
    private val labelStoragePath: Label by fxid()
    private val btnDownload: JFXButton by fxid()
    private val btnChangePath: JFXButton by fxid()
    private val btnOpenDir: JFXButton by fxid()
    private val cbDownloadDefaultFormat: JFXCheckBox by fxid()
    private val cbDownloadPlaylist: JFXCheckBox by fxid()
    private val tbYoutubeDL: JFXToggleButton by fxid()
    private val tbYouGet: JFXToggleButton by fxid()
    private val labelYoutubeDL: Label by fxid()
    private val labelYouGet: Label by fxid()
    private val btnUpdateCore: JFXButton by fxid()
    private val cbSocks5: JFXCheckBox by fxid()
    private val tfSocksAddress: JFXTextField by fxid()
    private val tfSocksPort: JFXTextField by fxid()
    private val cbHTTP: JFXCheckBox by fxid()
    private val tfHTTPAddress: JFXTextField by fxid()
    private val tfHTTPPort: JFXTextField by fxid()
    private val labelVersion: Label by fxid()
    private val labelGitHub: Label by fxid()
    private val labelLicense: Label by fxid()
    private val labelAuthor: Label by fxid()
    private val btnDonate: JFXButton by fxid()
    private val btnReportBug: JFXButton by fxid()
    private val btnUpdate: JFXButton by fxid()


    init {
        // Window boarder
        primaryStage.initStyle(StageStyle.UNDECORATED)
        paneExit.setOnMouseClicked {
            Platform.exit()
        }

        paneMinimize.setOnMouseClicked {
            primaryStage.isIconified = true
        }

        apBorder.setOnMousePressed { event: MouseEvent? ->
            event?.let {
                xOffset = event.sceneX
                yOffset = event.sceneY
            }
        }

        apBorder.setOnMouseDragged { event: MouseEvent? ->
            event?.let {
                primaryStage.x = event.screenX - xOffset
                primaryStage.y = event.screenY - yOffset
            }
        }

        // init storage path
        labelStoragePath.text = safeLoadConfig(ContentUtils.STORAGE_PATH, Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString())

        btnChangePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                app.config[ContentUtils.STORAGE_PATH] = file.absolutePath.toString()
                app.config.save()
                labelStoragePath.text = file.absolutePath.toString()
            }
        }

        btnOpenDir.setOnMouseClicked {
            val dir = Paths.get(app.config[ContentUtils.STORAGE_PATH] as String).toFile()
            logger.debug("open dir: $dir")
            when (GUIPlatform.current()) {
                GUIPlatformType.LINUX -> {
                    Runtime.getRuntime().exec("xdg-open $dir")
                }
                GUIPlatformType.WINDOWS -> {
                    Desktop.getDesktop().open(dir)
                }
                GUIPlatformType.MAC_OS -> {
                    Desktop.getDesktop().open(dir)
                }
                GUIPlatformType.NOT_SUPPORTED -> {

                }
            }
        }

        // init download engine
        when (EngineType.valueOf(safeLoadConfig(EngineType.ENGINE_TYPE.name, EngineType.NONE.name))) {
            EngineType.YOUTUBE_DL -> {
                tbYoutubeDL.isSelected = true
            }
            EngineType.YOU_GET -> {
                tbYouGet.isSelected = true
            }
            else -> {
                app.config[EngineType.ENGINE_TYPE.name] = EngineType.YOUTUBE_DL.name
                app.config.save()
                tbYoutubeDL.isSelected = true
            }
        }

        // init version label
        labelYouGet.text = safeLoadConfig(EngineUtils.YOU_GET_VERSION, messages["noVersionInfo"])
        labelYoutubeDL.text = safeLoadConfig(EngineUtils.YOUTUBE_DL_VERSION, messages["noVersionInfo"])

        // engine toggle button listener
        tbYoutubeDL.action {
            if (tbYoutubeDL.isSelected) {
                app.config[EngineType.ENGINE_TYPE.name] = EngineType.YOUTUBE_DL.name
                app.config.save()
            }
        }
        tbYouGet.action {
            if (tbYouGet.isSelected) {
                app.config[EngineType.ENGINE_TYPE.name] = EngineType.YOU_GET.name
                app.config.save()
            }
        }

        // init updates listener
        btnUpdateCore.setOnMouseClicked {
            UpdatesView().openModal(StageStyle.UNDECORATED)
            fire(RequestCheckUpdatesYouGet)
            fire(RequestCheckUpdatesYoutubeDL)
        }

        btnUpdate.setOnMouseClicked {
            controller.updateGUI()
        }

        // init proxy
        tfSocksAddress.text = safeLoadConfig(ProxyType.SOCKS5_PROXY_ADDRESS.name, "")
        tfSocksPort.text = safeLoadConfig(ProxyType.SOCKS5_PROXY_PORT.name, "")
        tfHTTPAddress.text = safeLoadConfig(ProxyType.HTTP_PROXY_ADDRESS.name, "")
        tfHTTPPort.text = safeLoadConfig(ProxyType.HTTP_PROXY_PORT.name, "")

        when (ProxyType.valueOf(safeLoadConfig(ProxyType.PROXY_TYPE.name, ProxyType.NONE.name))) {
            ProxyType.SOCKS5 -> cbSocks5.isSelected = true
            ProxyType.HTTP -> cbHTTP.isSelected = true
            else -> {
                cbHTTP.isSelected = false
                cbSocks5.isSelected = false
            }
        }

        tfSocksAddress.focusedProperty().addListener({ _, oldValue, _ ->
            if (oldValue) { // when unfocused save to config
                app.config[ProxyType.SOCKS5_PROXY_ADDRESS.name] = tfSocksAddress.text
                app.config.save()
            }
        })

        tfSocksPort.focusedProperty().addListener({ _, oldValue, _ ->
            if (oldValue) {
                app.config[ProxyType.SOCKS5_PROXY_PORT.name] = tfSocksPort.text
                app.config.save()
            }
        })

        tfHTTPAddress.focusedProperty().addListener({ _, oldValue, _ ->
            if (oldValue) { // when unfocused save to config
                app.config[ProxyType.HTTP_PROXY_ADDRESS.name] = tfHTTPAddress.text
                app.config.save()
            }
        })

        tfHTTPPort.focusedProperty().addListener({ _, oldValue, _ ->
            if (oldValue) {
                app.config[ProxyType.HTTP_PROXY_PORT.name] = tfHTTPPort.text
                app.config.save()
            }
        })


        cbSocks5.action {
            // disable socks proxy
            if (!cbSocks5.isSelected) {
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.NONE.name
                app.config.save()
            }

            // enable socks proxy
            if (cbSocks5.isSelected) {
                cbHTTP.isSelected = false
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.SOCKS5.name
                app.config.save()
            }
        }

        cbHTTP.action {
            // disable http proxy
            if (!cbHTTP.isSelected) {
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.NONE.name
                app.config.save()
            }

            // enable http proxy
            if (cbHTTP.isSelected) {
                cbSocks5.isSelected = false
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.HTTP.name
                app.config.save()
            }
        }

        // init about view
        labelVersion.text = safeLoadConfig(ContentUtils.APP_VERSION, messages["noVersionInfo"])
        labelGitHub.setOnMouseClicked { hostServices.showDocument(ContentUtils.APP_SOURCE_CODE) }
        labelLicense.setOnMouseClicked { hostServices.showDocument(ContentUtils.APP_LICENSE) }
        labelAuthor.setOnMouseClicked { hostServices.showDocument(ContentUtils.APP_AUTHOR) }
        btnReportBug.action { hostServices.showDocument(ContentUtils.APP_REPORT_BUGS) }
        btnDonate.action { openInternalWindow(ImageView::class) }

        // init download settings
        cbDownloadDefaultFormat.isSelected = safeLoadConfig(ContentUtils.DOWNLOAD_DEFAULT, "false").toBoolean()
        cbDownloadDefaultFormat.action {
            app.config[ContentUtils.DOWNLOAD_DEFAULT] = cbDownloadDefaultFormat.isSelected.toString()
            app.config.save()
        }
        // TODO enable download playlist
        cbDownloadPlaylist.isDisable = true

        // fetch media json and display it
        btnDownload.setOnMouseClicked {
            if (tfURL.text != null && tfURL.text.trim() != "") {
                // load proxy settings
                val proxyType = ProxyType.valueOf(safeLoadConfig(ProxyType.PROXY_TYPE.name, ProxyType.NONE.name))
                var address = ""
                var port = ""
                when (proxyType) {
                    ProxyType.SOCKS5 -> {
                        address = safeLoadConfig(ProxyType.SOCKS5_PROXY_ADDRESS.name, "")
                        port = safeLoadConfig(ProxyType.SOCKS5_PROXY_PORT.name, "")
                    }

                    ProxyType.HTTP -> {
                        address = safeLoadConfig(ProxyType.HTTP_PROXY_ADDRESS.name, "")
                        port = safeLoadConfig(ProxyType.HTTP_PROXY_PORT.name, "")
                    }

                    ProxyType.NONE -> {
                    }

                    else -> {
                        logger.error("error proxy type $proxyType")
                    }
                }
                // load engine type
                val engineType = EngineType.valueOf(safeLoadConfig(EngineType.ENGINE_TYPE.name, EngineType.YOUTUBE_DL.name))
                // load output path
                val output = app.config.string(ContentUtils.STORAGE_PATH)
                // load download settings
                val downloadDefaultFormat = safeLoadConfig(ContentUtils.DOWNLOAD_DEFAULT, "false").toBoolean()
                // create current config instance
                val ccf = CurrentConfig(engineType, tfURL.text, proxyType, address, port, output, "")

                if (downloadDefaultFormat) {
                    // download default format directly
                    ProgressView().openWindow(StageStyle.UNDECORATED)
                    fire(DownloadMedia(ccf))
                } else {
                    // display the media list view
                    replaceWith(find<MediaListView>(mapOf("ccf" to ccf)), ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                }
            }
        }
    }

    private fun safeLoadConfig(key: String, defaultValue: String): String {
        return try {
            val value = app.config.string(key)
            if (value.isEmpty()) {
                throw IllegalStateException("empty value in config file")
            } else {
                return value
            }
        } catch (e: IllegalStateException) {
            app.config[key] = defaultValue
            app.config.save()
            defaultValue
        }
    }
}

package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
import com.ingbyr.guiyouget.engine.EngineType
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYouGet
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYoutubeDL
import com.ingbyr.guiyouget.utils.*
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXTextField
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.stage.StageStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.action
import tornadofx.seconds
import java.awt.Desktop
import java.lang.IllegalStateException
import java.nio.file.Paths
import java.util.*
import kotlin.collections.set


class MainView : View("GUI-YouGet") {
    // todo Skip the choice of formatID
    // todo Add download playlist function
    // todo Add enable log button
    // todo Some config needs to safe load

    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(MainView::class.java)
    }

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

    private val cbChooseDeafultFormat: JFXCheckBox by fxid()
    private val cbAsPlayList: JFXCheckBox by fxid()

    private val cbYoutubeDL: JFXCheckBox by fxid()
    private val cbYouGet: JFXCheckBox by fxid()
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

        // Storage path
        if (app.config[CommonUtils.STORAGE_PATH] == null || app.config[CommonUtils.STORAGE_PATH] == "") {
            labelStoragePath.text = Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString()
            app.config[CommonUtils.STORAGE_PATH] = labelStoragePath.text
            app.config.save()
        } else {
            labelStoragePath.text = app.config[CommonUtils.STORAGE_PATH] as String
        }

        btnChangePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                app.config[CommonUtils.STORAGE_PATH] = file.absolutePath.toString()
                app.config.save()
                labelStoragePath.text = file.absolutePath.toString()
            }
        }

        btnOpenDir.setOnMouseClicked {
            val dir = Paths.get(app.config[CommonUtils.STORAGE_PATH] as String).toFile()
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
                        logger.debug("no proxy from config")
                    }

                    else -> {
                        logger.error("error proxy type $proxyType")
                    }
                }

                // load engine type
                val engineType = ProxyType.valueOf(safeLoadConfig(EngineType.ENGINE_TYPE.name, EngineType.YOUTUBE_DL.name))
                // todo trans the engineType to media list view and progress view
                // display the media list view
                replaceWith(find<MediaListView>(mapOf("url" to tfURL.text, "proxyType" to proxyType, "address" to address, "port" to port, "engineType" to engineType)),
                        ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
            }
        }

        // load download engine config
        val engineType = EngineType.valueOf(safeLoadConfig(EngineType.ENGINE_TYPE.name, EngineType.YOUTUBE_DL.name))
        when (engineType) {
            EngineType.YOUTUBE_DL -> {
                cbYoutubeDL.isSelected = true
            }
            EngineType.YOU_GET -> {
                cbYouGet.isSelected = true
            }
            else -> {
                app.config[EngineType.ENGINE_TYPE.name] = EngineType.YOUTUBE_DL
                app.config.save()
                cbYoutubeDL.isSelected = true
            }
        }

        // init version label
        labelYouGet.text = app.config[EngineUtils.YOU_GET_VERSION] as String
        labelYoutubeDL.text = app.config[EngineUtils.YOUTUBE_DL_VERSION] as String

        // engine checkbox listener
        cbYoutubeDL.action {
            if (cbYoutubeDL.isSelected) {
                cbYouGet.isSelected = false
                app.config[EngineType.ENGINE_TYPE] = EngineType.YOUTUBE_DL
                app.config.save()
            }
        }
        cbYouGet.action {
            if (cbYouGet.isSelected) {
                cbYoutubeDL.isSelected = false
                app.config[EngineType.ENGINE_TYPE] = EngineType.YOU_GET
                app.config.save()
            }
        }

        // Updates listener
        btnUpdateCore.setOnMouseClicked {
            UpdatesView().openModal(StageStyle.UNDECORATED)
            fire(RequestCheckUpdatesYouGet)
            fire(RequestCheckUpdatesYoutubeDL)
        }

        btnUpdate.setOnMouseClicked {
            controller.updateGUI()
        }

        // Proxy
        tfSocksAddress.text = safeLoadConfig(ProxyType.SOCKS5_PROXY_ADDRESS.name)
        tfSocksPort.text = safeLoadConfig(ProxyType.SOCKS5_PROXY_PORT.name)
        tfHTTPAddress.text = safeLoadConfig(ProxyType.HTTP_PROXY_ADDRESS.name)
        tfHTTPPort.text = safeLoadConfig(ProxyType.HTTP_PROXY_PORT.name)

        val proxyType = ProxyType.valueOf(safeLoadConfig(ProxyType.PROXY_TYPE.name, ProxyType.NONE.name))
        when (proxyType) {
            ProxyType.SOCKS5 -> cbSocks5.isSelected = true
            ProxyType.HTTP -> cbHTTP.isSelected = true
            else -> {
                cbHTTP.isSelected = false
                cbSocks5.isSelected = false
            }
        }

        tfSocksAddress.focusedProperty().addListener({ _, oldValue, _ ->
            if (oldValue) { // When unfocused save to config
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
            if (oldValue) { // When unfocused save to config
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
            // Disable socks proxy
            if (!cbSocks5.isSelected) {
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.NONE.name
                app.config.save()
            }

            // Enable socks proxy
            if (cbSocks5.isSelected) {
                cbHTTP.isSelected = false
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.SOCKS5.name
                app.config.save()
            }
        }

        cbHTTP.action {
            // Disable http proxy
            if (!cbHTTP.isSelected) {
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.NONE.name
                app.config.save()
            }

            // Enable http proxy
            if (cbHTTP.isSelected) {
                cbSocks5.isSelected = false
                app.config[ProxyType.PROXY_TYPE.name] = ProxyType.HTTP.name
                app.config.save()
            }
        }

        // About view
        labelVersion.text = app.config[CommonUtils.APP_VERSION] as String
        labelGitHub.setOnMouseClicked { hostServices.showDocument(CommonUtils.APP_SOURCE_CODE) }
        labelLicense.setOnMouseClicked { hostServices.showDocument(CommonUtils.APP_LICENSE) }
        labelAuthor.setOnMouseClicked { hostServices.showDocument(CommonUtils.APP_AUTHOR) }
        btnReportBug.action { hostServices.showDocument(CommonUtils.APP_REPORT_BUGS) }
        btnDonate.action { openInternalWindow(ImageView::class) }
    }

    private fun safeLoadConfig(key: String, defaultValue: String = ""): String {
        return try {
            app.config.string(key)
        } catch (e: IllegalStateException) {
            app.config[key] = defaultValue
            app.config.save()
            defaultValue
        }
    }
}

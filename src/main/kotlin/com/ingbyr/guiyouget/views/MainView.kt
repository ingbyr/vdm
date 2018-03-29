package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
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
import tornadofx.*
import java.awt.Desktop
import java.nio.file.Paths
import java.util.*

class MainView : View("GUI-YouGet") {
    // todo Skip the choice of formatID
    // todo Add minimize icon to the progress view
    // todo Add download playlist function
    // todo log button

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
    private val apBorder: AnchorPane by fxid()

    private val tfURL: JFXTextField by fxid()

    private val labelStoragePath: Label by fxid()
    private val labelYoutubeDL: Label by fxid()
    private val labelYouGet: Label by fxid()
    private val labelAPP: Label by fxid()

    private val btnDownload: JFXButton by fxid()
    private val btnChangePath: JFXButton by fxid()
    private val btnOpenDir: JFXButton by fxid()
    private val btnUpdateCore: JFXButton by fxid()
    private val btnReportBug: JFXButton by fxid()
    private val btnUpdate: JFXButton by fxid()

    private val cbYoutubeDL: JFXCheckBox by fxid()
    private val cbYouGet: JFXCheckBox by fxid()

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


    init {
        // Window boarder
        primaryStage.initStyle(StageStyle.UNDECORATED)
        paneExit.setOnMouseClicked {
            Platform.exit()
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

        // Get media list
        btnDownload.setOnMouseClicked {
            if (tfURL.text != null && tfURL.text.trim() != "") {
//                replaceWith(MediaListView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                replaceWith(find<MediaListView>(mapOf("url" to tfURL.text)), ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
            }
        }

        // Load download engine config
        val core = app.config[EngineUtils.TYPE]
        when (core) {
            EngineUtils.YOUTUBE_DL -> {
                cbYoutubeDL.isSelected = true
            }
            EngineUtils.YOU_GET -> {
                cbYouGet.isSelected = true
            }
            else -> {
                app.config[EngineUtils.TYPE] = EngineUtils.YOUTUBE_DL
                app.config.save()
                cbYoutubeDL.isSelected = true
            }
        }

        // Init version
        labelYouGet.text = app.config[EngineUtils.YOU_GET_VERSION] as String
        labelYoutubeDL.text = app.config[EngineUtils.YOUTUBE_DL_VERSION] as String
        labelAPP.text = app.config[CommonUtils.APP_VERSION] as String

        // Change download engine
        cbYouGet.action {
            if (cbYouGet.isSelected) {
                cbYoutubeDL.isSelected = false
                app.config[EngineUtils.TYPE] = EngineUtils.YOU_GET
                app.config.save()
            }
        }

        cbYoutubeDL.action {
            if (cbYoutubeDL.isSelected) {
                cbYouGet.isSelected = false
                app.config[EngineUtils.TYPE] = EngineUtils.YOUTUBE_DL
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
        val proxy = app.config[ProxyUtils.TYPE]
        when (proxy) {
            ProxyUtils.HTTP -> {
                cbHTTP.isSelected = true
                tfHTTPAddress.text = app.config[ProxyUtils.ADDRESS] as String
                tfHTTPPort.text = app.config[ProxyUtils.PORT] as String
            }
            ProxyUtils.SOCKS5 -> {
                cbSocks5.isSelected = true
                tfSocksAddress.text = app.config[ProxyUtils.ADDRESS] as String
                tfSocksPort.text = app.config[ProxyUtils.PORT] as String
            }
            else -> {
                cbHTTP.isSelected = false
                cbSocks5.isSelected = false
            }
        }

        tfSocksAddress.textProperty().addListener { _, _, newValue ->
            if (app.config[ProxyUtils.TYPE] == ProxyUtils.SOCKS5) {
                app.config[ProxyUtils.ADDRESS] = newValue
                app.config.save()
            }
        }

        tfSocksPort.textProperty().addListener { _, _, newValue ->
            if (app.config[ProxyUtils.TYPE] == ProxyUtils.SOCKS5) {
                app.config[ProxyUtils.PORT] = newValue
                app.config.save()
            }
        }

        tfHTTPAddress.textProperty().addListener { _, _, newValue ->
            if (app.config[ProxyUtils.TYPE] == ProxyUtils.HTTP) {
                app.config[ProxyUtils.ADDRESS] = newValue
                app.config.save()
            }
        }

        tfHTTPPort.textProperty().addListener { _, _, newValue ->
            if (app.config[ProxyUtils.TYPE] == ProxyUtils.HTTP) {
                app.config[ProxyUtils.PORT] = newValue
                app.config.save()
            }
        }

        cbSocks5.action {
            val address = tfSocksAddress.text
            val port = tfSocksPort.text
            // Disable socks proxy
            if (!cbSocks5.isSelected) {
                app.config[ProxyUtils.TYPE] = ProxyUtils.NONE
                app.config.save()
            }

            // Enable socks proxy
            if (cbSocks5.isSelected) {
                cbHTTP.isSelected = false
                app.config[ProxyUtils.TYPE] = ProxyUtils.SOCKS5
                app.config[ProxyUtils.ADDRESS] = address
                app.config[ProxyUtils.PORT] = port
                app.config.save()
            }
        }

        cbHTTP.action {
            val address = tfHTTPAddress.text
            val port = tfHTTPPort.text
            // Disable http proxy
            if (!cbHTTP.isSelected) {
                app.config[ProxyUtils.TYPE] = ProxyUtils.NONE
                app.config.save()
            }

            // Enable http proxy
            if (cbHTTP.isSelected) {
                cbSocks5.isSelected = false
                app.config[ProxyUtils.TYPE] = ProxyUtils.HTTP
                app.config[ProxyUtils.ADDRESS] = address
                app.config[ProxyUtils.PORT] = port
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
}

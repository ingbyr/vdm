package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYouGet
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYoutubeDL
import com.ingbyr.guiyouget.utils.ContentsUtil
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
import tornadofx.*
import java.awt.Desktop
import java.nio.file.Paths
import java.util.*

class MainView : View("GUI-YouGet") {
    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
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
        if (app.config[ContentsUtil.STORAGE_PATH] == null || app.config[ContentsUtil.STORAGE_PATH] == "") {
            labelStoragePath.text = Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString()
            app.config[ContentsUtil.STORAGE_PATH] = labelStoragePath.text
            app.config.save()
        } else {
            labelStoragePath.text = app.config[ContentsUtil.STORAGE_PATH] as String
        }

        btnChangePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                app.config[ContentsUtil.STORAGE_PATH] = file.absolutePath.toString()
                app.config.save()
                labelStoragePath.text = file.absolutePath.toString()
            }
        }

        btnOpenDir.setOnMouseClicked {
            val dir = Paths.get(app.config[ContentsUtil.STORAGE_PATH] as String).toFile()
            Desktop.getDesktop().open(dir)
        }

        // Get media list
        btnDownload.setOnMouseClicked {
            if (tfURL.text != null && tfURL.text.trim() != "") {
                replaceWith(MediaListView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                controller.requestMediaInfo(tfURL.text)
            }
        }

        // Load download engine config
        val core = app.config[ContentsUtil.DOWNLOAD_CORE]
        when (core) {
            ContentsUtil.YOUTUBE_DL -> {
                cbYoutubeDL.isSelected = true
            }
            ContentsUtil.YOU_GET -> {
                cbYouGet.isSelected = true
            }
            else -> {
                app.config[ContentsUtil.DOWNLOAD_CORE] = ContentsUtil.YOUTUBE_DL
                app.config.save()
                cbYoutubeDL.isSelected = true
            }
        }

        // Init version
        labelYouGet.text = app.config[ContentsUtil.YOU_GET_VERSION] as String
        labelYoutubeDL.text = app.config[ContentsUtil.YOUTUBE_DL_VERSION] as String
        labelAPP.text = app.config[ContentsUtil.APP_VERSION] as String

        // Change download engine
        cbYouGet.action {
            if (cbYouGet.isSelected) {
                cbYoutubeDL.isSelected = false
                app.config[ContentsUtil.DOWNLOAD_CORE] = ContentsUtil.YOU_GET
                app.config.save()
            }
        }

        cbYoutubeDL.action {
            if (cbYoutubeDL.isSelected) {
                cbYouGet.isSelected = false
                app.config[ContentsUtil.DOWNLOAD_CORE] = ContentsUtil.YOUTUBE_DL
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
        val proxy = app.config[ContentsUtil.PROXY_TYPE]
        when (proxy) {
            ContentsUtil.PROXY_HTTP -> {
                cbHTTP.isSelected = true
                tfHTTPAddress.text = app.config[ContentsUtil.PROXY_ADDRESS] as String
                tfHTTPPort.text = app.config[ContentsUtil.PROXY_PORT] as String
            }
            ContentsUtil.PROXY_SOCKS -> {
                cbSocks5.isSelected = true
                tfSocksAddress.text = app.config[ContentsUtil.PROXY_ADDRESS] as String
                tfSocksPort.text = app.config[ContentsUtil.PROXY_PORT] as String
            }
            else -> {
                cbHTTP.isSelected = false
                cbSocks5.isSelected = false
            }
        }

        tfSocksAddress.textProperty().addListener { _, _, newValue ->
            if (app.config[ContentsUtil.PROXY_TYPE] == ContentsUtil.PROXY_SOCKS) {
                app.config[ContentsUtil.PROXY_ADDRESS] = newValue
                app.config.save()
            }
        }

        tfSocksPort.textProperty().addListener { _, _, newValue ->
            if (app.config[ContentsUtil.PROXY_TYPE] == ContentsUtil.PROXY_SOCKS) {
                app.config[ContentsUtil.PROXY_PORT] = newValue
                app.config.save()
            }
        }

        tfHTTPAddress.textProperty().addListener { _, _, newValue ->
            if (app.config[ContentsUtil.PROXY_TYPE] == ContentsUtil.PROXY_HTTP) {
                app.config[ContentsUtil.PROXY_ADDRESS] = newValue
                app.config.save()
            }
        }

        tfHTTPPort.textProperty().addListener { _, _, newValue ->
            if (app.config[ContentsUtil.PROXY_TYPE] == ContentsUtil.PROXY_HTTP) {
                app.config[ContentsUtil.PROXY_PORT] = newValue
                app.config.save()
            }
        }

        cbSocks5.action {
            val address = tfSocksAddress.text
            val port = tfSocksPort.text
            // Disable socks proxy
            if (!cbSocks5.isSelected) {
                app.config[ContentsUtil.PROXY_TYPE] = ""
                app.config.save()
            }

            // Enable socks proxy
            if (cbSocks5.isSelected) {
                cbHTTP.isSelected = false
                app.config[ContentsUtil.PROXY_TYPE] = ContentsUtil.PROXY_SOCKS
                app.config[ContentsUtil.PROXY_ADDRESS] = address
                app.config[ContentsUtil.PROXY_PORT] = port
                app.config.save()
            }
        }

        cbHTTP.action {
            val address = tfHTTPAddress.text
            val port = tfHTTPPort.text
            // Disable http proxy
            if (!cbHTTP.isSelected) {
                app.config[ContentsUtil.PROXY_TYPE] = ""
                app.config.save()
            }

            // Enable http proxy
            if (cbHTTP.isSelected) {
                cbSocks5.isSelected = false
                app.config[ContentsUtil.PROXY_TYPE] = ContentsUtil.PROXY_HTTP
                app.config[ContentsUtil.PROXY_ADDRESS] = address
                app.config[ContentsUtil.PROXY_PORT] = port
                app.config.save()
            }
        }

        // About view
        labelVersion.text = app.config[ContentsUtil.APP_VERSION] as String
        labelGitHub.setOnMouseClicked { hostServices.showDocument(ContentsUtil.APP_SOURCE_CODE) }
        labelLicense.setOnMouseClicked { hostServices.showDocument(ContentsUtil.APP_LICENSE) }
        labelAuthor.setOnMouseClicked { hostServices.showDocument(ContentsUtil.APP_AUTHOR) }
        btnReportBug.action { hostServices.showDocument(ContentsUtil.APP_REPORT_BUGS) }
        btnDonate.action { openInternalWindow(ImageView::class)}
    }

    // clean the url textfield
    override fun onDock() {
        tfURL.text = ""
    }
}

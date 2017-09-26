package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYouGet
import com.ingbyr.guiyouget.events.RequestCheckUpdatesYoutubeDL
import com.ingbyr.guiyouget.utils.CoreUtils
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
import java.nio.file.Paths

class MainView : View("GUI-YouGet") {
    private var xOffset = 0.0
    private var yOffset = 0.0

    override val root: AnchorPane by fxml("/fxml/MainWindow.fxml")

    private val controller: MainController by inject()

    private val tfURL: JFXTextField by fxid()

    private val labelStoragePath: Label by fxid()
    private val labelVersion: Label by fxid()
    private val labelYoutubeDL: Label by fxid()
    private val labelYouGet: Label by fxid()

    private val btnDownload: JFXButton by fxid()
    private val btnChangePath: JFXButton by fxid()
    private val btnUpdateCore: JFXButton by fxid()
    private val btnUpdate: JFXButton by fxid()

    private val cbYoutubeDL: JFXCheckBox by fxid()
    private val cbYouGet: JFXCheckBox by fxid()

    private val paneExit: Pane by fxid()
    private val apBorder: AnchorPane by fxid()

    private val cbSocks5: JFXCheckBox by fxid()
    private val tfSocksAddress: JFXTextField by fxid()
    private val tfSocksPort: JFXTextField by fxid()
    private val cbHTTP: JFXCheckBox by fxid()
    private val tfHTTPAddress: JFXTextField by fxid()
    private val tfHTTPPort: JFXTextField by fxid()

    private val labelAboutVersion: Label by fxid()
    private val labelGitHub: Label by fxid()
    private val labelLicense: Label by fxid()
    private val labelAuthor: Label by fxid()
    private val btnFollowMe: JFXButton by fxid()
    private val btnReportBug: JFXButton by fxid()
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
        if (app.config["storagePath"] == null) {
            labelStoragePath.text = Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString()
            app.config["storagePath"] = labelStoragePath.text
            app.config.save()
        } else {
            labelStoragePath.text = app.config["storagePath"] as String
        }

        btnChangePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            if (file != null) {
                app.config["storagePath"] = file.absolutePath.toString()
                app.config.save()
                labelStoragePath.text = file.absolutePath.toString()
            }
        }

        // Get media list
        btnDownload.setOnMouseClicked {
            if (tfURL.text != null && tfURL.text.trim() != "") {
                replaceWith(MediaListView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                controller.requestMediaInfo(tfURL.text)
            }
        }

        // Load download core config
        val core = app.config["core"]
        when (core) {
            CoreUtils.YOUTUBE_DL -> {
                cbYoutubeDL.isSelected = true
                CoreUtils.current = CoreUtils.YOUTUBE_DL
            }
            CoreUtils.YOU_GET -> {
                cbYouGet.isSelected = true
                CoreUtils.current = CoreUtils.YOU_GET
            }
            else -> {
                app.config["core"] = CoreUtils.YOUTUBE_DL
                app.config.save()
                cbYoutubeDL.isSelected = true
                CoreUtils.current = CoreUtils.YOUTUBE_DL
            }
        }

        // Init core version
        labelYouGet.text = app.config[CoreUtils.YOU_GET_VERSION] as String
        labelYoutubeDL.text = app.config[CoreUtils.YOUTUBE_DL_VERSION] as String

        // Init app version
        labelVersion.text = app.config[CoreUtils.APP_VERSION] as String

        // Change download core
        cbYouGet.action {
            if (cbYouGet.isSelected) {
                cbYoutubeDL.isSelected = false
                app.config["core"] = CoreUtils.YOU_GET
                app.config.save()
            }
        }

        cbYoutubeDL.action {
            if (cbYoutubeDL.isSelected) {
                cbYouGet.isSelected = false
                app.config["core"] = CoreUtils.YOUTUBE_DL
                app.config.save()
            }
        }

        // Updates listener
        btnUpdateCore.setOnMouseClicked {
            UpdatesWindow().openModal(StageStyle.UNDECORATED)
            fire(RequestCheckUpdatesYouGet)
            fire(RequestCheckUpdatesYoutubeDL)
        }

        btnUpdate.setOnMouseClicked {
            controller.updateGUI()
        }

        // Proxy
        val proxy = app.config[CoreUtils.PROXY_TYPE]
        when (proxy) {
            CoreUtils.PROXY_HTTP -> {
                cbHTTP.isSelected = true
                tfHTTPAddress.text = app.config[CoreUtils.PROXY_ADDRESS] as String
                tfHTTPPort.text = app.config[CoreUtils.PROXY_PORT] as String
            }
            CoreUtils.PROXY_SOCKS -> {
                cbSocks5.isSelected = true
                tfSocksAddress.text = app.config[CoreUtils.PROXY_ADDRESS] as String
                tfSocksPort.text = app.config[CoreUtils.PROXY_PORT] as String
            }
            else -> {
                cbHTTP.isSelected = false
                cbSocks5.isSelected = false
            }
        }

        cbSocks5.action {
            val address = tfSocksAddress.text
            val port = tfSocksPort.text
            // Disable socks proxy
            if (!cbSocks5.isSelected) {
                app.config[CoreUtils.PROXY_TYPE] = ""
                app.config.save()
            }

            // Enable socks proxy
            if (cbSocks5.isSelected && address.trim() != "" && port.trim() != "") {
                cbHTTP.isSelected = false
                app.config[CoreUtils.PROXY_TYPE] = CoreUtils.PROXY_SOCKS
                app.config[CoreUtils.PROXY_ADDRESS] = address
                app.config[CoreUtils.PROXY_PORT] = port
                app.config.save()
            } else {
                cbSocks5.isSelected = false
            }
        }

        cbHTTP.action {
            val address = tfHTTPAddress.text
            val port = tfHTTPPort.text
            // Disable http proxy
            if (!cbHTTP.isSelected) {
                app.config[CoreUtils.PROXY_TYPE] = ""
                app.config.save()
            }

            // Enable http proxy
            if (cbHTTP.isSelected && address.trim() != "" && port.trim() != "") {
                cbSocks5.isSelected = false
                app.config[CoreUtils.PROXY_TYPE] = CoreUtils.PROXY_HTTP
                app.config[CoreUtils.PROXY_ADDRESS] = address
                app.config[CoreUtils.PROXY_PORT] = port
                app.config.save()
            } else {
                cbHTTP.isSelected = false
            }
        }

        // About view
        labelAboutVersion.text = app.config[CoreUtils.APP_VERSION] as String
        labelGitHub.setOnMouseClicked { hostServices.showDocument(CoreUtils.APP_SOURCE_CODE) }
        labelLicense.setOnMouseClicked { hostServices.showDocument(CoreUtils.APP_LICENSE) }
        labelAuthor.setOnMouseClicked { hostServices.showDocument(CoreUtils.APP_AUTHOR) }
        btnFollowMe.action { hostServices.showDocument(CoreUtils.APP_AUTHOR_GITHUB) }
        btnReportBug.action { hostServices.showDocument(CoreUtils.APP_REPORT_BUGS) }
        btnDonate.action { hostServices.showDocument(CoreUtils.APP_DONATE) }
    }

    // clean the url textfield
    override fun onDock() {
        tfURL.text = ""
    }
}

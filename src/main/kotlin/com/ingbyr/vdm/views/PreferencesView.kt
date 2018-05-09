package com.ingbyr.vdm.views

import com.jfoenix.controls.JFXTabPane
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXToggleButton
import javafx.scene.control.Label
import javafx.scene.control.ToggleGroup
import tornadofx.*
import java.util.*


class PreferencesView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    override val root: JFXTabPane by fxml("/fxml/PreferencesView.fxml")

    private val btnDownloadDefault: JFXToggleButton by fxid()
    private val tbYoutubeDL: JFXToggleButton by fxid()
    private val engines: ToggleGroup by fxid()
    private val labelYoutbeDLVersion: Label by fxid()
    private val tbYouGet: JFXToggleButton by fxid()
    private val labelYouGetVersion: Label by fxid()
    private val tbYkdl: JFXToggleButton by fxid()
    private val labelYkdlVersion: Label by fxid()
    private val tbSocks5: JFXToggleButton by fxid()
    private val proxy: ToggleGroup by fxid()
    private val tfSocks5Address: JFXTextField by fxid()
    private val tfSocks5Port: JFXTextField by fxid()
    private val tbHTTP: JFXToggleButton by fxid()
    private val tfHTTPAddress: JFXTextField by fxid()
    private val tfHTTPPort: JFXTextField by fxid()
    private val labelStoragePath: Label by fxid()
    private val labelFfmpegPath: Label by fxid()

    init {

    }
}
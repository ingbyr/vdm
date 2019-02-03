package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.PreferencesController
import com.ingbyr.vdm.controllers.ThemeController
import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.RefreshCookieContent
import com.ingbyr.vdm.events.RefreshEngineVersion
import com.ingbyr.vdm.events.RestorePreferencesViewEvent
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.utils.Attributes
import com.ingbyr.vdm.utils.config.engine
import com.ingbyr.vdm.utils.config.proxy
import com.ingbyr.vdm.utils.config.update
import com.jfoenix.controls.*
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.stage.DirectoryChooser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.nio.charset.Charset
import java.util.*


class PreferencesView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
        title = messages["ui.preferences"]
    }

    override val root: JFXTabPane by fxml("/fxml/PreferencesView.fxml")
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val controller: PreferencesController by inject()
    private val themeController: ThemeController by inject()

    private val labelStoragePath: Label by fxid()
    private val btnChangeStoragePath: JFXButton by fxid()
    private val labelFFMPEGPath: Label by fxid()
    private val btnChangeFFMPEGPath: JFXButton by fxid()
    private val tbDownloadDefault: JFXToggleButton by fxid()
    private val tbEnableDebug: JFXToggleButton by fxid()
    private val themeSelector: JFXComboBox<String> by fxid()
    private val charsetSelector: JFXComboBox<String> by fxid()

    private val tbYoutubeDL: JFXToggleButton by fxid()
    private val btnUpdateYoutubeDL: JFXButton by fxid()
    private val labelYoutubeDLVersion: Label by fxid()
    private val tbAnnie: JFXToggleButton by fxid()
    private val btnUpdateAnnie: JFXButton by fxid()
    private val labelAnnieVersion: Label by fxid()

    private val tbSocks5: JFXToggleButton by fxid()
    private val tfSocks5Address: JFXTextField by fxid()
    private val tfSocks5Port: JFXTextField by fxid()
    private val tbHTTP: JFXToggleButton by fxid()
    private val tfHTTPAddress: JFXTextField by fxid()
    private val tfHTTPPort: JFXTextField by fxid()

    private val cookieToggleButton: JFXToggleButton by fxid()
    private val newCookieButton: JFXButton by fxid()
    private val editCookieButton: JFXButton by fxid()
    private val deleteCookieButton: JFXButton by fxid()
    private val cookieComboBox: JFXComboBox<String> by fxid()
    private val cookieTextArea: TextArea by fxid()


    init {
        subEvents()
        loadVDMConfig()
        initListeners()
    }

    private fun subEvents() {
        subscribe<RefreshEngineVersion> {
            when (it.engineType) {
                EngineType.YOUTUBE_DL -> {
                    labelYoutubeDLVersion.text = it.newVersion
                }
                EngineType.ANNIE -> {
                    labelAnnieVersion.text = it.newVersion
                }
            }
        }

        subscribe<RestorePreferencesViewEvent> {
            logger.debug("restore preferences view")
            this@PreferencesView.currentStage?.isIconified = false
        }
    }

    private fun loadVDMConfig() {
        // download settings area
        labelStoragePath.text = app.config.string(Attributes.STORAGE_PATH, Attributes.Defaults.STORAGE_PATH)
        labelFFMPEGPath.text = app.config.string(Attributes.FFMPEG_PATH, Attributes.Defaults.FFMPEG_PATH)
        tbDownloadDefault.isSelected =
            app.config.boolean(Attributes.DOWNLOAD_DEFAULT_FORMAT, Attributes.Defaults.DOWNLOAD_DEFAULT_FORMAT)

        // engines settings area
        val engineType = app.config.engine(Attributes.ENGINE_TYPE, Attributes.Defaults.ENGINE_TYPE)
        when (engineType) {
            EngineType.YOUTUBE_DL -> tbYoutubeDL.isSelected = true
            EngineType.ANNIE -> tbAnnie.isSelected = true
        }

        labelYoutubeDLVersion.text = app.config.string(Attributes.YOUTUBE_DL_VERSION, Attributes.Defaults.ENGINE_VERSION)
        labelAnnieVersion.text = app.config.string(Attributes.ANNIE_VERSION, Attributes.Defaults.ENGINE_VERSION)
        // proxy settings area
        val proxyType = app.config.proxy(Attributes.PROXY_TYPE, Attributes.Defaults.PROXY_TYPE)
        when (proxyType) {
            ProxyType.SOCKS5 -> tbSocks5.isSelected = true
            ProxyType.HTTP -> tbHTTP.isSelected = true
            ProxyType.NONE -> {
            }
        }

        tfSocks5Address.text = app.config.string(Attributes.SOCKS5_PROXY_ADDRESS, Attributes.Defaults.SOCKS5_PROXY_ADDRESS)
        tfSocks5Port.text = app.config.string(Attributes.SOCKS5_PROXY_PORT, Attributes.Defaults.SOCKS5_PROXY_PORT)
        tfHTTPAddress.text = app.config.string(Attributes.HTTP_PROXY_ADDRESS, Attributes.Defaults.HTTP_PROXY_ADDRESS)
        tfHTTPPort.text = app.config.string(Attributes.HTTP_PROXY_PORT, Attributes.Defaults.HTTP_PROXY_PORT)

        // debug mode
        tbEnableDebug.selectedProperty().bindBidirectional(controller.debugModeProperty)

        // whether to use cookie
        cookieToggleButton.isSelected = app.config.boolean(Attributes.ENABLE_COOKIE, Attributes.Defaults.ENABLE_COOKIE)

        // TODO init theme selector
//        themeSelector.items.addAll(themeController.themes)
//        themeSelector.bind(themeController.activeThemeProperty)

        // init charset
        charsetSelector.items.addAll(Charset.availableCharsets().keys.toList()) // TODO wrap in func?
        charsetSelector.selectionModel.select(app.config.string(Attributes.CHARSET, Attributes.Defaults.CHARSET))
    }

    private fun initListeners() {
        // general settings area
        btnChangeStoragePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                val newPath = this.absoluteFile.toString()
                app.config[Attributes.STORAGE_PATH] = newPath
                labelStoragePath.text = newPath
            }
        }
        btnChangeFFMPEGPath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                val newPath = this.absoluteFile.toString()
                app.config[Attributes.FFMPEG_PATH] = newPath
                labelFFMPEGPath.text = newPath
            }
        }
        tbDownloadDefault.action {
            app.config.update(Attributes.DOWNLOAD_DEFAULT_FORMAT, tbDownloadDefault.isSelected)
        }

        // engines settings area
        tbYoutubeDL.whenSelected {
            app.config.update(Attributes.ENGINE_TYPE, EngineType.YOUTUBE_DL)
        }
        tbAnnie.whenSelected {
            app.config.update(Attributes.ENGINE_TYPE, EngineType.ANNIE)
        }
        btnUpdateYoutubeDL.setOnMouseClicked {
            controller.updateEngine(
                EngineType.YOUTUBE_DL,
                app.config.string(Attributes.YOUTUBE_DL_VERSION, Attributes.Defaults.ENGINE_VERSION)
            )
            this.currentStage?.isIconified = true
        }
        btnUpdateAnnie.setOnMouseClicked {
            controller.updateEngine(
                EngineType.ANNIE,
                app.config.string(Attributes.ANNIE_VERSION, Attributes.Defaults.ENGINE_VERSION)
            )
            this.currentStage?.isIconified = true
        }

        // proxy settings area
        tbSocks5.action {
            if (tbSocks5.isSelected) {
                app.config.update(Attributes.PROXY_TYPE, ProxyType.SOCKS5)
            } else if (!tbHTTP.isSelected) {
                app.config.update(Attributes.PROXY_TYPE, ProxyType.NONE)
            }
        }
        tbHTTP.action {
            if (tbHTTP.isSelected) {
                app.config.update(Attributes.PROXY_TYPE, ProxyType.HTTP)
            } else if (!tbSocks5.isSelected) {
                app.config.update(Attributes.PROXY_TYPE, ProxyType.NONE)
            }
        }

        // charset
        charsetSelector.selectionModel.selectedItemProperty().addListener { _, _, newCharset ->
            app.config.update(Attributes.CHARSET, newCharset)
        }

        // cookie
        cookieTextArea.bind(controller.cookieProperty)
        cookieToggleButton.action {
            app.config.update(Attributes.ENABLE_COOKIE, cookieToggleButton.isSelected)
        }
        cookieComboBox.items = controller.cookieList
        cookieComboBox.selectionModel.select(app.config.string(Attributes.CURRENT_COOKIE, Attributes.Defaults.COOKIE))
        cookieComboBox.selectionModel.selectedItemProperty().addListener { _, _, cookieName ->
            cookieName?.let {
                app.config.update(Attributes.CURRENT_COOKIE, it)
                controller.readCookieContent()
            }
        }
        newCookieButton.setOnMouseClicked {
            //            find<TextEditorView>(
//                mapOf(
//                    "fileEditorOption" to FileEditorOption(Attributes.COOKIES_DIR, true, ".txt")
//                )
//            ).openWindow()
        }
        editCookieButton.setOnMouseClicked {
            //            find<TextEditorView>(
//                mapOf(
//                    "fileEditorOption" to FileEditorOption(
//                        Attributes.COOKIES_DIR.resolve(ConfigUtils.load(Attributes.CURRENT_COOKIE)),
//                        false,
//                        ".txt"
//                    )
//                )
//            ).openWindow()
        }
        subscribe<RefreshCookieContent> {
            controller.freshCookieListAndContent()
            cookieComboBox.selectionModel.select(app.config.string(Attributes.CURRENT_COOKIE, Attributes.Defaults.COOKIE))
        }
    }

    /**
     * Save the app to the file when close this view
     */
    override fun onUndock() {
        super.onUndock()
        app.config.update(Attributes.SOCKS5_PROXY_ADDRESS, tfSocks5Address.text)
        app.config.update(Attributes.SOCKS5_PROXY_PORT, tfSocks5Port.text)
        app.config.update(Attributes.HTTP_PROXY_ADDRESS, tfHTTPAddress.text)
        app.config.update(Attributes.HTTP_PROXY_PORT, tfHTTPPort.text)
    }
}
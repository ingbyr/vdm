package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.PreferencesController
import com.ingbyr.vdm.controllers.ThemeController
import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.RefreshCookieContent
import com.ingbyr.vdm.events.RefreshEngineVersion
import com.ingbyr.vdm.events.RestorePreferencesViewEvent
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.utils.Attributes
import com.ingbyr.vdm.utils.ConfigUtils
import com.ingbyr.vdm.utils.EngineConfigUtils
import com.ingbyr.vdm.utils.FileEditorOption
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
        labelStoragePath.text = ConfigUtils.safeLoad(Attributes.STORAGE_PATH, Attributes.APP_DIR)
        labelFFMPEGPath.text = ConfigUtils.safeLoad(Attributes.FFMPEG_PATH, "")
        tbDownloadDefault.isSelected = ConfigUtils.safeLoad(Attributes.DOWNLOAD_DEFAULT_FORMAT, "false").toBoolean()

        // engines settings area
        val engineType = EngineType.valueOf(ConfigUtils.safeLoad(Attributes.ENGINE_TYPE, EngineType.YOUTUBE_DL))
        when (engineType) {
            EngineType.YOUTUBE_DL -> tbYoutubeDL.isSelected = true
            EngineType.ANNIE -> tbAnnie.isSelected = true
        }

        labelYoutubeDLVersion.text = EngineConfigUtils.safeLoad(Attributes.YOUTUBE_DL_VERSION, "0.0.0")
        labelAnnieVersion.text = EngineConfigUtils.safeLoad(Attributes.ANNIE_VERSION, "0.0.0")
        // proxy settings area
        val proxyType = ProxyType.valueOf(ConfigUtils.safeLoad(Attributes.PROXY_TYPE, ProxyType.NONE))
        when (proxyType) {
            ProxyType.SOCKS5 -> tbSocks5.isSelected = true
            ProxyType.HTTP -> tbHTTP.isSelected = true
            ProxyType.NONE -> {
            }
        }

        tfSocks5Address.text = ConfigUtils.safeLoad(Attributes.SOCKS5_PROXY_ADDRESS, "")
        tfSocks5Port.text = ConfigUtils.safeLoad(Attributes.SOCKS5_PROXY_PORT, "")
        tfHTTPAddress.text = ConfigUtils.safeLoad(Attributes.HTTP_PROXY_ADDRESS, "")
        tfHTTPPort.text = ConfigUtils.safeLoad(Attributes.HTTP_PROXY_PORT, "")

        // debug mode
        tbEnableDebug.selectedProperty().bindBidirectional(controller.debugModeProperty)

        // cookie
        cookieToggleButton.isSelected = ConfigUtils.safeLoad(Attributes.ENABLE_COOKIE, "false").toBoolean()

        // TODO init theme selector
//        themeSelector.items.addAll(themeController.themes)
//        themeSelector.bind(themeController.activeThemeProperty)

        // init charset
        charsetSelector.items.addAll(Charset.availableCharsets().keys.toList())
        charsetSelector.selectionModel.select(ConfigUtils.safeLoad(Attributes.CHARSET, "UTF-8"))
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
            ConfigUtils.update(Attributes.DOWNLOAD_DEFAULT_FORMAT, tbDownloadDefault.isSelected)
        }

        // engines settings area
        tbYoutubeDL.whenSelected {
            ConfigUtils.update(Attributes.ENGINE_TYPE, EngineType.YOUTUBE_DL)
        }
        tbAnnie.whenSelected {
            ConfigUtils.update(Attributes.ENGINE_TYPE, EngineType.ANNIE)
        }
        btnUpdateYoutubeDL.setOnMouseClicked {
             controller.updateEngine(EngineType.YOUTUBE_DL, EngineConfigUtils.safeLoad(Attributes.YOUTUBE_DL_VERSION, "0.0.0"))
            this.currentStage?.isIconified = true
        }
        btnUpdateAnnie.setOnMouseClicked {
            controller.updateEngine(EngineType.ANNIE, EngineConfigUtils.safeLoad(Attributes.ANNIE_VERSION, "0.0.0"))
            this.currentStage?.isIconified = true
        }

        // proxy settings area
        tbSocks5.action {
            if (tbSocks5.isSelected) {
                ConfigUtils.update(Attributes.PROXY_TYPE, ProxyType.SOCKS5)
            } else if (!tbHTTP.isSelected) {
                ConfigUtils.update(Attributes.PROXY_TYPE, ProxyType.NONE)
            }
        }
        tbHTTP.action {
            if (tbHTTP.isSelected) {
                ConfigUtils.update(Attributes.PROXY_TYPE, ProxyType.HTTP)
            } else if (!tbSocks5.isSelected) {
                ConfigUtils.update(Attributes.PROXY_TYPE, ProxyType.NONE)
            }
        }

        // charset
        charsetSelector.selectionModel.selectedItemProperty().addListener { _, _, newCharset ->
            ConfigUtils.update(Attributes.CHARSET, newCharset)
        }

        // cookie
        cookieTextArea.bind(controller.cookieProperty)
        cookieToggleButton.action {
            ConfigUtils.update(Attributes.ENABLE_COOKIE, cookieToggleButton.isSelected)
        }
        cookieComboBox.items = controller.cookieList
        cookieComboBox.selectionModel.select(ConfigUtils.safeLoad(Attributes.CURRENT_COOKIE, ""))
        cookieComboBox.selectionModel.selectedItemProperty().addListener { _, _, cookieName ->
            cookieName?.let {
                ConfigUtils.update(Attributes.CURRENT_COOKIE, it)
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
            cookieComboBox.selectionModel.select(ConfigUtils.safeLoad(Attributes.CURRENT_COOKIE, ""))
        }
    }

    /**
     * Save the config to the file when close this view
     */
    override fun onUndock() {
        super.onUndock()
        ConfigUtils.update(Attributes.SOCKS5_PROXY_ADDRESS, tfSocks5Address.text)
        ConfigUtils.update(Attributes.SOCKS5_PROXY_PORT, tfSocks5Port.text)
        ConfigUtils.update(Attributes.HTTP_PROXY_ADDRESS, tfHTTPAddress.text)
        ConfigUtils.update(Attributes.HTTP_PROXY_PORT, tfHTTPPort.text)
    }
}
package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.PreferencesController
import com.ingbyr.vdm.controllers.ThemeController
import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.RefreshCookieContent
import com.ingbyr.vdm.events.RefreshEngineVersion
import com.ingbyr.vdm.events.RestorePreferencesViewEvent
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.utils.AppConfigUtils
import com.ingbyr.vdm.utils.AppProperties
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


    private val cu = AppConfigUtils(app.config)

    init {
        subEvents()
        loadVDMConfig()
        initListeners()
    }

    private fun subEvents() {
        // TODO update the version info
        subscribe<RefreshEngineVersion> {
            when (it.engineType) {
                EngineType.YOUTUBE_DL -> {
                    labelYoutubeDLVersion.text = it.newVersion
//                    EnginesJsonUtils.engineInfo(AppProperties.YOUTUBE_DL).version = it.newVersion
                }
                EngineType.ANNIE -> {
                    labelAnnieVersion.text = it.newVersion
//                    EnginesJsonUtils.engineInfo(AppProperties.ANNIE).version = it.newVersion
                }
                else -> {
                }
            }
//            EnginesJsonUtils.save2JsonFile()
        }

        subscribe<RestorePreferencesViewEvent> {
            logger.debug("restore preferences view")
            this@PreferencesView.currentStage?.isIconified = false
        }
    }

    private fun loadVDMConfig() {
        // download settings area
        labelStoragePath.text = cu.safeLoad(AppProperties.STORAGE_PATH, AppProperties.APP_DIR)
        labelFFMPEGPath.text = cu.safeLoad(AppProperties.FFMPEG_PATH, "")
        tbDownloadDefault.isSelected = cu.safeLoad(AppProperties.DOWNLOAD_DEFAULT_FORMAT, "false").toBoolean()

        // engines settings area
        val engineType = EngineType.valueOf(cu.safeLoad(AppProperties.ENGINE_TYPE, EngineType.YOUTUBE_DL))
        when (engineType) {
            EngineType.YOUTUBE_DL -> tbYoutubeDL.isSelected = true
            EngineType.ANNIE -> tbAnnie.isSelected = true
            else -> logger.error("no engines type of $engineType")
        }

        // TODO display the version
        // labelYoutubeDLVersion.text = EnginesJsonUtils.engineInfo(AppProperties.YOUTUBE_DL).version
        // labelAnnieVersion.text = EnginesJsonUtils.engineInfo(AppProperties.ANNIE).version

        // proxy settings area
        val proxyType = ProxyType.valueOf(cu.safeLoad(AppProperties.PROXY_TYPE, ProxyType.NONE))
        when (proxyType) {
            ProxyType.SOCKS5 -> tbSocks5.isSelected = true
            ProxyType.HTTP -> tbHTTP.isSelected = true
            ProxyType.NONE -> {
            }
        }

        tfSocks5Address.text = cu.safeLoad(AppProperties.SOCKS5_PROXY_ADDRESS, "")
        tfSocks5Port.text = cu.safeLoad(AppProperties.SOCKS5_PROXY_PORT, "")
        tfHTTPAddress.text = cu.safeLoad(AppProperties.HTTP_PROXY_ADDRESS, "")
        tfHTTPPort.text = cu.safeLoad(AppProperties.HTTP_PROXY_PORT, "")

        // debug mode
        tbEnableDebug.selectedProperty().bindBidirectional(controller.debugModeProperty)

        // cookie
        cookieToggleButton.isSelected = cu.safeLoad(AppProperties.ENABLE_COOKIE, "false").toBoolean()

        // TODO init theme selector
//        themeSelector.items.addAll(themeController.themes)
//        themeSelector.bind(themeController.activeThemeProperty)

        // init charset
        charsetSelector.items.addAll(Charset.availableCharsets().keys.toList())
        charsetSelector.selectionModel.select(cu.safeLoad(AppProperties.CHARSET, "UTF-8"))
    }

    private fun initListeners() {
        // general settings area
        btnChangeStoragePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                val newPath = this.absoluteFile.toString()
                app.config[AppProperties.STORAGE_PATH] = newPath
                labelStoragePath.text = newPath
            }
        }
        btnChangeFFMPEGPath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                val newPath = this.absoluteFile.toString()
                app.config[AppProperties.FFMPEG_PATH] = newPath
                labelFFMPEGPath.text = newPath
            }
        }
        tbDownloadDefault.action {
            cu.update(AppProperties.DOWNLOAD_DEFAULT_FORMAT, tbDownloadDefault.isSelected)
        }

        // engines settings area
        tbYoutubeDL.whenSelected {
            cu.update(AppProperties.ENGINE_TYPE, EngineType.YOUTUBE_DL)
        }
        tbAnnie.whenSelected {
            cu.update(AppProperties.ENGINE_TYPE, EngineType.ANNIE)
        }
        btnUpdateYoutubeDL.setOnMouseClicked {
            // TODO update engine with local version info
            // controller.updateEngine(EngineType.YOUTUBE_DL, EnginesJsonUtils.engineInfo(AppProperties.YOUTUBE_DL).version)
            this.currentStage?.isIconified = true
        }
        btnUpdateAnnie.setOnMouseClicked {
            // TODO update engine with local version info
            // controller.updateEngine(EngineType.ANNIE, EnginesJsonUtils.engineInfo(AppProperties.ANNIE).version)
            this.currentStage?.isIconified = true
        }

        // proxy settings area
        tbSocks5.action {
            if (tbSocks5.isSelected) {
                cu.update(AppProperties.PROXY_TYPE, ProxyType.SOCKS5)
            } else if (!tbHTTP.isSelected) {
                cu.update(AppProperties.PROXY_TYPE, ProxyType.NONE)
            }
        }
        tbHTTP.action {
            if (tbHTTP.isSelected) {
                cu.update(AppProperties.PROXY_TYPE, ProxyType.HTTP)
            } else if (!tbSocks5.isSelected) {
                cu.update(AppProperties.PROXY_TYPE, ProxyType.NONE)
            }
        }

        // charset
        charsetSelector.selectionModel.selectedItemProperty().addListener { _, _, newCharset ->
            cu.update(AppProperties.CHARSET, newCharset)
        }

        // cookie
        cookieTextArea.bind(controller.cookieProperty)
        cookieToggleButton.action {
            cu.update(AppProperties.ENABLE_COOKIE, cookieToggleButton.isSelected)
        }
        cookieComboBox.items = controller.cookieList
        cookieComboBox.selectionModel.select(cu.safeLoad(AppProperties.CURRENT_COOKIE, ""))
        cookieComboBox.selectionModel.selectedItemProperty().addListener { _, _, cookieName ->
            cookieName?.let {
                cu.update(AppProperties.CURRENT_COOKIE, it)
                controller.readCookieContent()
            }
        }
        newCookieButton.setOnMouseClicked {
            find<FileEditorView>(mapOf(
                    "fileEditorOption" to FileEditorOption(AppProperties.COOKIES_DIR, true, ".txt"))
            ).openWindow()
        }
        editCookieButton.setOnMouseClicked {
            find<FileEditorView>(mapOf(
                    "fileEditorOption" to FileEditorOption(AppProperties.COOKIES_DIR.resolve(cu.load(AppProperties.CURRENT_COOKIE)), false, ".txt"))
            ).openWindow()
        }
        subscribe<RefreshCookieContent> {
            controller.freshCookieListAndContent()
            cookieComboBox.selectionModel.select(cu.safeLoad(AppProperties.CURRENT_COOKIE, ""))
        }
    }

    /**
     * Save the config to the file when close this view
     */
    override fun onUndock() {
        super.onUndock()
        cu.update(AppProperties.SOCKS5_PROXY_ADDRESS, tfSocks5Address.text)
        cu.update(AppProperties.SOCKS5_PROXY_PORT, tfSocks5Port.text)
        cu.update(AppProperties.HTTP_PROXY_ADDRESS, tfHTTPAddress.text)
        cu.update(AppProperties.HTTP_PROXY_PORT, tfHTTPPort.text)
    }
}
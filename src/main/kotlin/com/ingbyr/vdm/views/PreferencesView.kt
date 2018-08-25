package com.ingbyr.vdm.views

import ch.qos.logback.classic.Level
import com.ingbyr.vdm.controllers.PreferencesController
import com.ingbyr.vdm.controllers.ThemeController
import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.RefreshEngineVersion
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.utils.AppConfigUtils
import com.ingbyr.vdm.utils.AppProperties
import com.ingbyr.vdm.utils.EnginesJsonUtils
import com.jfoenix.controls.*
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
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
    private val themeSelector : JFXComboBox<String> by fxid()
    private val charsetSelector : JFXComboBox<String> by fxid()

    private val tbYoutubeDL: JFXToggleButton by fxid()
    private val btnUpdateYoutubeDL: JFXButton by fxid()
    private val labelYoutubeDLVersion: Label by fxid()
    private val tbYouGet: JFXToggleButton by fxid()
    private val btnUpdateYouGet: JFXButton by fxid()
    private val labelYouGetVersion: Label by fxid()

    private val tbSocks5: JFXToggleButton by fxid()
    private val tfSocks5Address: JFXTextField by fxid()
    private val tfSocks5Port: JFXTextField by fxid()
    private val tbHTTP: JFXToggleButton by fxid()
    private val tfHTTPAddress: JFXTextField by fxid()
    private val tfHTTPPort: JFXTextField by fxid()

    private val cu = AppConfigUtils(app.config)

    init {
        subscribe<RefreshEngineVersion> {
            when (it.engineType) {
                EngineType.YOUTUBE_DL -> {
                    labelYoutubeDLVersion.text = it.newVersion
                    EnginesJsonUtils.engineInfo(AppProperties.YOUTUBE_DL).version = it.newVersion
                }
                EngineType.YOU_GET -> { // todo change to annie
                    labelYouGetVersion.text = it.newVersion
                    EnginesJsonUtils.engineInfo(AppProperties.ANNIE).version = it.newVersion
                }
                else -> {
                }
            }
            EnginesJsonUtils.save2JsonFile()
        }

        loadVDMConfig()
        initListeners()
        initSelectorContent()
    }

    private fun initSelectorContent() {
        // init theme selector
        themeSelector.items.addAll(themeController.themes)
        themeSelector.bind(themeController.activeThemeProperty)

        // init charset
        charsetSelector.items.addAll(Charset.availableCharsets().keys.toList())
        charsetSelector.selectionModel.select(cu.safeLoad(AppProperties.CHARSET, "UTF-8"))
        charsetSelector.selectionModel.selectedItemProperty().addListener { _, _, newCharset ->
            cu.update(AppProperties.CHARSET, newCharset)
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
            EngineType.YOU_GET -> tbYouGet.isSelected = true
            else -> logger.error("no engines type of $engineType")
        }
        labelYoutubeDLVersion.text = EnginesJsonUtils.engineInfo(AppProperties.YOUTUBE_DL).version
        // todo change to annie downloader
        labelYouGetVersion.text = EnginesJsonUtils.engineInfo(AppProperties.ANNIE).version

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
        tbEnableDebug.isSelected = cu.safeLoad(AppProperties.DEBUG_MODE, "false").toBoolean()
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
        tbEnableDebug.action {
            val rootLogger = LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
            if (tbEnableDebug.isSelected) {
                rootLogger.level = Level.DEBUG
                cu.update(AppProperties.DEBUG_MODE, true)
            } else {
                rootLogger.level = Level.ERROR
                cu.update(AppProperties.DEBUG_MODE, false)
            }
        }

        // engines settings area
        tbYoutubeDL.whenSelected {
            cu.update(AppProperties.ENGINE_TYPE, EngineType.YOUTUBE_DL)
        }
        tbYouGet.whenSelected {
            cu.update(AppProperties.ENGINE_TYPE, EngineType.YOU_GET)
        }
        btnUpdateYoutubeDL.setOnMouseClicked {
            controller.updateEngine(EngineType.YOUTUBE_DL, EnginesJsonUtils.engineInfo(AppProperties.YOUTUBE_DL).version)
            this.currentStage?.isIconified = true // todo not work perfectly
        }
        btnUpdateYouGet.setOnMouseClicked {
            controller.updateEngine(EngineType.YOU_GET, EnginesJsonUtils.engineInfo(AppProperties.ANNIE).version)
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
    }

    private fun saveTextFieldContent() {
        cu.update(AppProperties.SOCKS5_PROXY_ADDRESS, tfSocks5Address.text)
        cu.update(AppProperties.SOCKS5_PROXY_PORT, tfSocks5Port.text)
        cu.update(AppProperties.HTTP_PROXY_ADDRESS, tfHTTPAddress.text)
        cu.update(AppProperties.HTTP_PROXY_PORT, tfHTTPPort.text)
    }

    /**
     * Save the config to the file when close this view
     */
    override fun onUndock() {
        super.onUndock()
        saveTextFieldContent()
    }
}
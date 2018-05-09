package com.ingbyr.vdm.views

import com.ingbyr.vdm.utils.*
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTabPane
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXToggleButton
import javafx.scene.control.Label
import javafx.stage.DirectoryChooser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*


class PreferencesView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    override val root: JFXTabPane by fxml("/fxml/PreferencesView.fxml")
    private val logger: Logger = LoggerFactory.getLogger(PreferencesView::class.java)

    private val labelStoragePath: Label by fxid()
    private val btnChangeStoragePath: JFXButton by fxid()
    private val labelFFMPEGPath: Label by fxid()
    private val btnChangeFFMPEGPath: JFXButton by fxid()
    private val tbDownloadDefault: JFXToggleButton by fxid()

    private val tbYoutubeDL: JFXToggleButton by fxid()
    private val labelYoutubeDLVersion: Label by fxid()
    private val tbYouGet: JFXToggleButton by fxid()
    private val labelYouGetVersion: Label by fxid()

    private val tbSocks5: JFXToggleButton by fxid()
    private val tfSocks5Address: JFXTextField by fxid()
    private val tfSocks5Port: JFXTextField by fxid()
    private val tbHTTP: JFXToggleButton by fxid()
    private val tfHTTPAddress: JFXTextField by fxid()
    private val tfHTTPPort: JFXTextField by fxid()

    private val cu = VDMConfigUtils(app.config)

    init {
        loadVDMConfig()
        initListeners()
    }

    private fun loadVDMConfig() {

        // download settings area
        labelStoragePath.text = cu.safeLoad(VDMConfigUtils.STORAGE_PATH, VDMContent.APP_DIR)
        labelFFMPEGPath.text = cu.safeLoad(VDMConfigUtils.FFMPEG_PATH, VDMContent.NONE)
        tbDownloadDefault.isSelected = cu.safeLoad(VDMConfigUtils.DOWNLOAD_DEFAULT_FORMAT, "false").toBoolean()

        // engine settings area
        val engineType = EngineType.valueOf(cu.safeLoad(VDMConfigUtils.ENGINE_TYPE, EngineType.YOUTUBE_DL))
        when (engineType) {
            EngineType.YOUTUBE_DL -> tbYoutubeDL.isSelected = true
            EngineType.YOU_GET -> tbYouGet.isSelected = true
            else -> logger.error("no engine type of $engineType")
        }
        labelYoutubeDLVersion.text = cu.safeLoad(EngineUtils.YOUTUBE_DL_VERSION, messages["unknown"])
        labelYouGetVersion.text = cu.safeLoad(EngineUtils.YOU_GET_VERSION, messages["unknown"])


        // proxy settings area
        val proxyType = ProxyType.valueOf(cu.safeLoad(VDMConfigUtils.PROXY_TYPE, ProxyType.NONE))
        when (proxyType) {
            ProxyType.SOCKS5 -> tbSocks5.isSelected = true
            ProxyType.HTTP -> tbHTTP.isSelected = true
            ProxyType.NONE -> {
            }
        }
        // TODO add validations for proxy textfield
        tfSocks5Address.text = cu.safeLoad(VDMConfigUtils.SOCKS5_PROXY_ADDRESS, "")
        tfSocks5Port.text = cu.safeLoad(VDMConfigUtils.SOCKS5_PROXY_PORT, "")
        tfHTTPAddress.text = cu.safeLoad(VDMConfigUtils.HTTP_PROXY_ADDRESS, "")
        tfHTTPPort.text = cu.safeLoad(VDMConfigUtils.HTTP_PROXY_PORT, "")
    }

    private fun initListeners() {
        // download settings area
        btnChangeStoragePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                val newPath = this.absoluteFile.toString()
                app.config[VDMConfigUtils.STORAGE_PATH] = newPath
                labelStoragePath.text = newPath
            }
        }
        btnChangeFFMPEGPath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            file?.apply {
                val newPath = this.absoluteFile.toString()
                app.config[VDMConfigUtils.FFMPEG_PATH] = newPath
                labelStoragePath.text = newPath
            }
        }
        tbDownloadDefault.action {
            cu.update(VDMConfigUtils.DOWNLOAD_DEFAULT_FORMAT, tbDownloadDefault.isSelected)
        }

        // engine settings area
        tbYoutubeDL.whenSelected {
            cu.update(VDMConfigUtils.ENGINE_TYPE, EngineType.YOUTUBE_DL)
        }
        tbYouGet.whenSelected {
            cu.update(VDMConfigUtils.ENGINE_TYPE, EngineType.YOU_GET)
        }

        // proxy settings area
        tbSocks5.action {
            if (tbSocks5.isSelected) {
                cu.update(VDMConfigUtils.PROXY_TYPE, ProxyType.SOCKS5)
            } else if (!tbHTTP.isSelected) {
                cu.update(VDMConfigUtils.PROXY_TYPE, ProxyType.NONE)
            }
        }
        tbHTTP.action {
            if (tbHTTP.isSelected) {
                cu.update(VDMConfigUtils.PROXY_TYPE, ProxyType.HTTP)
            } else if (!tbSocks5.isSelected) {
                cu.update(VDMConfigUtils.PROXY_TYPE, ProxyType.NONE)
            }
        }
    }

    private fun saveTextFieldContent() {
        cu.update(VDMConfigUtils.SOCKS5_PROXY_ADDRESS, tfSocks5Address.text)
        cu.update(VDMConfigUtils.SOCKS5_PROXY_PORT, tfSocks5Port.text)
        cu.update(VDMConfigUtils.HTTP_PROXY_ADDRESS, tfHTTPAddress.text)
        cu.update(VDMConfigUtils.HTTP_PROXY_PORT, tfHTTPPort.text)
    }

    /**
     * Save the config to the file when close this view
     */
    override fun onUndock() {
        super.onUndock()
        saveTextFieldContent()
        cu.saveToConfigFile()
    }
}
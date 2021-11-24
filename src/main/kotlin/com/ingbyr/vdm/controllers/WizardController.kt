package com.ingbyr.vdm.controllers


import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.events.UpdateEngineTask
import com.ingbyr.vdm.utils.Attributes
import com.ingbyr.vdm.utils.config.update
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class WizardController : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    private val logger: Logger = LoggerFactory.getLogger(WizardController::class.java)
    private val themeController: ThemeController by inject()

    // summary info
    private val enginesNeedDownload = mutableSetOf<EngineType>()
    private val commonSettingChanges = mutableMapOf<String, String>()

    fun addEngine(engine: String) {
        enginesNeedDownload.add(EngineType.valueOf(engine.replace("-", "_").toUpperCase()))
    }

    fun removeEngine(engine: String) {
        enginesNeedDownload.remove(EngineType.valueOf(engine.replace("-", "_").toUpperCase()))
    }

    fun changePrimaryColor(color: String) {
        app.config.update(Attributes.THEME_PRIMARY_COLOR, color)
        themeController.reloadTheme()
        commonSettingChanges[messages["ui.primaryColor"]] = color
    }

    fun changeSecondaryColor(color: String) {
        app.config.update(Attributes.THEME_SECONDARY_COLOR, color)
        themeController.reloadTheme()
        commonSettingChanges[messages["ui.secondaryColor"]] = color
    }

    fun changeStoragePath(storagePath: String) {
        app.config.update(Attributes.STORAGE_PATH, storagePath)
        commonSettingChanges[messages["ui.storagePath"]] = storagePath
    }

    fun summary(): String {
        val sb = StringBuilder()
        // engines
        sb.appendln("${messages["ui.installEngines"]}: $enginesNeedDownload")
        sb.appendln()

        // TODO ui display color in a better way
        commonSettingChanges.forEach { configName, config -> sb.appendln("$configName ${messages["ui.changeTo"]} $config") }
        return sb.toString()
    }

    fun startDownloadSelectedEngines() {
        enginesNeedDownload.forEach {
            fire(UpdateEngineTask(it, "0.0.0"))
        }
    }
}
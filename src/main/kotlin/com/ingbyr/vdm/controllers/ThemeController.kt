package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.utils.AppConfigUtils
import com.ingbyr.vdm.utils.AppProperties
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

open class ThemeController : Controller() {
    val logger = LoggerFactory.getLogger(ThemeController::class.java)

    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    val themesNameAndUrl = mapOf(
            messages["theme.default"] to "DefaultTheme".resourceUrl(),
            messages["theme.defaultBigger"] to "DefaultThemeBigger".resourceUrl()
    )
    val themes = SimpleListProperty<String>(themesNameAndUrl.keys.toList().observable())
    val activeThemeProperty = SimpleStringProperty()
    var activeTheme by activeThemeProperty

    private val cu = AppConfigUtils(app.config)

    fun initTheme() {
        // load theme config
        activeTheme = cu.safeLoad(AppProperties.THEME, messages["theme.default"])
        importStylesheet(themesNameAndUrl[activeTheme] ?: "DefaultTheme".resourceUrl())

        // add listener to change theme
        activeThemeProperty.addListener { _, oldTheme, newTheme ->
            logger.debug("change theme $oldTheme to $newTheme")
            oldTheme?.run {
                // remove old theme
                val css = FX::class.java.getResource(themesNameAndUrl[oldTheme])
                FX.stylesheets.remove(css.toExternalForm())
            }

            themesNameAndUrl[newTheme]?.run {
                importStylesheet(this)
                // save to config
                cu.update(AppProperties.THEME, newTheme)
            }
        }
    }

    private fun String.resourceUrl() = "/fxml/themes/$this.css"
}
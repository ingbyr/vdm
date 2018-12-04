package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.stylesheets.DarkTheme
import com.ingbyr.vdm.stylesheets.LightTheme
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*
import kotlin.reflect.KClass

open class ThemeController : Controller() {
    private val logger: Logger = LoggerFactory.getLogger(ThemeController::class.java)

    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    val themes = SimpleListProperty<KClass<out Stylesheet>>(listOf(LightTheme::class, DarkTheme::class).observable())
    val activeThemeProperty = SimpleObjectProperty<KClass<out Stylesheet>>()
    var activeTheme by activeThemeProperty


    fun initTheme() {
        // add listener to change theme
        activeThemeProperty.addListener { _, oldTheme, newTheme ->
            oldTheme?.let{ removeStylesheet(it)}
            newTheme?.let { importStylesheet(it) }
        }

        // TODO load theme from config
        activeTheme = themes.first()

    }

    fun reloadTheme() {
        removeStylesheet(activeTheme)
        importStylesheet(activeTheme)
    }
}
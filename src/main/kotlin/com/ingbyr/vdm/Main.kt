package com.ingbyr.vdm

import com.ingbyr.vdm.controllers.ThemeController
import com.ingbyr.vdm.utils.AppProperties
import com.ingbyr.vdm.views.MainView
import com.ingbyr.vdm.views.WizardView
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class MainApp : App(MainView::class) {
    override val configPath: Path = AppProperties.configFilePath
    private val availableLanguages = listOf("zh", "en", "hu")
    private val themeController : ThemeController by inject()

    init {
//         Locale.setDefault(Locale("en", ""))
        val language = Locale.getDefault().language
        if (language !in availableLanguages) {
            Locale.setDefault(Locale("en", "US"))
        }
    }

    override fun start(stage: Stage) {
        super.start(stage)
        themeController.initTheme()
        addStageIcon(Image("/imgs/logo.jpg"))
    }
}


fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}
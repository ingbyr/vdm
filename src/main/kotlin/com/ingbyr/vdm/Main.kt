package com.ingbyr.vdm

import com.ingbyr.vdm.utils.AppProperties
import com.ingbyr.vdm.views.MainView
import javafx.application.Application
import javafx.scene.image.Image
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Main : App(MainView::class) {
    override val configPath: Path = AppProperties.configFilePath
    private val availableLanguages = listOf("zh", "en", "hu")

    init {
//         Locale.setDefault(Locale("en", ""))
        val language = Locale.getDefault().language
        if (language !in availableLanguages) {
            Locale.setDefault(Locale("en", "US"))
        }
        addStageIcon(Image("/imgs/logo.jpg"))
    }
}


fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
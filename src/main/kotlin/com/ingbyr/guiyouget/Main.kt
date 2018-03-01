package com.ingbyr.guiyouget

import com.ingbyr.guiyouget.views.MainView
import javafx.application.Application
import org.slf4j.LoggerFactory
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class Main : App(MainView::class) {
    private val logger = LoggerFactory.getLogger(Main::class.java)
    override val configBasePath: Path = Paths.get(System.getProperty("user.dir"))
    private val availableLanguages = listOf("zh", "en", "hu")

    init {
//        todo comment this
//        Locale.setDefault(Locale("test", "test"))
        val language = Locale.getDefault().language
        if (language !in availableLanguages) {
            Locale.setDefault(Locale("en", "US"))
        }

        val prop = System.getProperties()
        logger.debug("OS: ${prop["os.name"]?.toString()} Arch: ${prop["os.arch"]?.toString()} Version: ${prop["os.version"]?.toString()}")
        logger.debug("JAVA: ${prop["java.version"]?.toString()} Vender: ${prop["java.vendor"]?.toString()}")
        logger.debug("Default Locale: ${FX.locale} Current Locale:${Locale.getDefault().language}_${Locale.getDefault().country}")
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
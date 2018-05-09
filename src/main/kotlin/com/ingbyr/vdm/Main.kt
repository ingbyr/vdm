package com.ingbyr.vdm

import com.ingbyr.vdm.utils.VDMContent
import com.ingbyr.vdm.views.MainView
import javafx.application.Application
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class Main : App(MainView::class) {
    private val logger = LoggerFactory.getLogger(Main::class.java)
    override val configBasePath = VDMContent.USER_DIR
    private val availableLanguages = listOf("zh", "en", "hu")

    init {

        // TODO i18n debug mode
        // Locale.setDefault(Locale("test", "test"))

        val language = Locale.getDefault().language
        if (language !in availableLanguages) {
            Locale.setDefault(Locale("en", "US"))
        }


        // TODO uncomment this
//        val prop = System.getProperties()
//        logger.debug("OS: ${prop["os.name"]?.toString()} Arch: ${prop["os.arch"]?.toString()} Version: ${prop["os.version"]?.toString()}")
//        logger.debug("JAVA: ${prop["java.version"]?.toString()} Vender: ${prop["java.vendor"]?.toString()}")
//        logger.debug("Default Locale: ${FX.locale} Current Locale:${Locale.getDefault().language}_${Locale.getDefault().country}")
//        logger.debug("Save config file to $configBasePath")

    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
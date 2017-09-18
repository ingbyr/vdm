package com.ingbyr.guiyouget

import com.ingbyr.guiyouget.views.MainView
import javafx.application.Application
import tornadofx.*

class Main : App(MainView::class) {
    init {
        //todo Internationalization https://edvin.gitbooks.io/tornadofx-guide/content/10.%20FXML.html
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
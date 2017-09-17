package com.ingbyr.guiyouget

import com.ingbyr.guiyouget.views.MainView
import javafx.application.Application
import tornadofx.*

class Main : App(MainView::class)

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
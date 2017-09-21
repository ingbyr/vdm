package com.ingbyr.guiyouget

import com.ingbyr.guiyouget.views.MainView
import javafx.application.Application
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class Main : App(MainView::class) {
    override val configBasePath: Path = Paths.get(System.getProperty("user.dir"))
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
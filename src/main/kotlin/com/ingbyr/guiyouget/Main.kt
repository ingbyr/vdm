package com.ingbyr.guiyouget

import com.ingbyr.guiyouget.views.MainView
import javafx.application.Application
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

class Main : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.UNDECORATED)
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.ProgressController
import com.ingbyr.guiyouget.events.UpdateMediaProgressbar
import com.jfoenix.controls.JFXProgressBar
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import tornadofx.*


class ProgressView : View() {
    val controller: ProgressController by inject()
    override val root: AnchorPane by fxml("/fxml/ProgressWindow.fxml")

    val progressbar: JFXProgressBar by fxid()
    val paneExit: Pane by fxid()
    val labelTitle: Label by fxid()
    val labelSpeed: Label by fxid()
    val labelTime: Label by fxid()

    init {
        paneExit.setOnMouseClicked {
            this.close()
        }

        subscribe<UpdateMediaProgressbar> {
            progressbar.progress = it.progress / 100
            labelSpeed.text = it.speed
            labelTime.text = it.extTime
        }

    }
}

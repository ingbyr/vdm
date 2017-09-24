package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.UpdatesController
import com.ingbyr.guiyouget.events.StopDownloading
import com.ingbyr.guiyouget.events.UpdateStates
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import tornadofx.*

class UpdatesWindow : View() {
    val controller: UpdatesController by inject()
    override val root: AnchorPane by fxml("/fxml/UpdatesWindow.fxml")

    private val labelStatus: Label by fxid()
    private val paneExit: Pane by fxid()

    init {
        controller.subscribeEvents()

        paneExit.setOnMouseClicked {
            fire(StopDownloading)
            this.close()
        }

        subscribe<UpdateStates> {
            labelStatus.text = it.status
        }
    }
}
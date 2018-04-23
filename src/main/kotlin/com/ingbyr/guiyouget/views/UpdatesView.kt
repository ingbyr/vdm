package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.UpdatesController
import com.ingbyr.guiyouget.events.StopBackgroundTask
import com.ingbyr.guiyouget.events.UpdateYouGetStates
import com.ingbyr.guiyouget.events.UpdateYoutubeDLStates
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import tornadofx.*
import java.util.*

class UpdatesView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/UpdatesView")
    }

    private val controller: UpdatesController by inject()
    override val root: AnchorPane by fxml("/fxml/UpdatesWindow.fxml")

    private val labelYouget: Label by fxid()
    private val labelYoutubedl: Label by fxid()
    private val paneExit: Pane by fxid()

    init {
        controller.subscribeEvents()

        paneExit.setOnMouseClicked {
            fire(StopBackgroundTask)
            this.close()
        }

        subscribe<UpdateYouGetStates> {
            labelYouget.text = it.status
        }

        subscribe<UpdateYoutubeDLStates> {
            labelYoutubedl.text = it.status
        }
    }
}
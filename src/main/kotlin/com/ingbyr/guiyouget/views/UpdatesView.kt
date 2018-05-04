package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.UpdatesController
import javafx.scene.layout.AnchorPane
import tornadofx.View
import java.util.*

class UpdatesView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/UpdatesView")
    }

    private val controller: UpdatesController by inject()
    override val root: AnchorPane by fxml("/fxml/UpdatesWindow.fxml")


    init {

    }
}
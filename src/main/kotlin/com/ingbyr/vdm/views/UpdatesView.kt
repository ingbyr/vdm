package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.UpdatesController
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
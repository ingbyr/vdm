package com.ingbyr.guiyouget.views

import com.jfoenix.controls.JFXTabPane
import tornadofx.*
import java.util.*

class PreferencesView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    override val root: JFXTabPane by fxml("/fxml/PreferencesView.fxml")

    init {

    }
}
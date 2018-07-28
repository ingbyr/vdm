package com.ingbyr.vdm.views

import com.ingbyr.vdm.utils.VDMConfigUtils
import com.ingbyr.vdm.utils.VDMProperties
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.*
import java.util.*

class AboutView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/AboutView")
        title = messages["ui.about"]
    }

    override val root: VBox by fxml("/fxml/AboutView.fxml")
    private val cu = VDMConfigUtils(app.config)
    private val labelVersion: Label by fxid()
    private val labelLicense: Label by fxid()
    private val labelSourceCode: Label by fxid()
    private val btnUpdate: JFXButton by fxid()
    private val btnReport: JFXButton by fxid()

    init {
        labelVersion.text = cu.load(VDMConfigUtils.VDM_VERSION)
        initListeners()
    }

    private fun initListeners() {
        labelLicense.setOnMouseClicked {
            hostServices.showDocument(VDMProperties.VDM_LICENSE)
        }
        labelSourceCode.setOnMouseClicked {
            hostServices.showDocument(VDMProperties.VDM_SOURCE_CODE)
        }
        btnUpdate.setOnMouseClicked {
            hostServices.showDocument(VDMProperties.VDM_UPDATE_URL)
        }
        btnReport.setOnMouseClicked {
            hostServices.showDocument(VDMProperties.VDM_REPORT_BUGS)
        }
    }
}
package com.ingbyr.guiyouget.views

import com.jfoenix.controls.JFXButton
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import tornadofx.View

class TipView : View() {
    override val root: AnchorPane by fxml("/fxml/TipWindow.fxml")
    private val labelTitle: Label by fxid()
    private val labelContent: Label by fxid()
    private val btnClose: JFXButton by fxid()
    private val apBorder: AnchorPane by fxid()

    private var xOffset = 0.0
    private var yOffset = 0.0

    private val myTitle: String = params["title"] as String
    private val myContent: String = params["content"] as String
    private val myBtnText: String = params["btnText"] as String

    init {
        apBorder.setOnMousePressed { event: MouseEvent? ->
            event?.let {
                xOffset = it.sceneX
                yOffset = it.sceneY
            }
        }

        apBorder.setOnMouseDragged { event: MouseEvent? ->
            event?.let {
                this.currentStage?.x = it.screenX - xOffset
                this.currentStage?.y = it.screenY - yOffset
            }
        }

        labelTitle.text = myTitle
        labelContent.text = myContent
        btnClose.text = myBtnText

        btnClose.setOnMouseClicked {
            this.close()
        }
    }
}
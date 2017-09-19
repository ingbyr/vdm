package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.DirectoryChooser
import javafx.stage.StageStyle
import tornadofx.*

class MainView : View("GUI-YouGet") {
    var xOffset = 0.0
    var yOffset = 0.0
    lateinit var args: Array<String>

    override val root: AnchorPane by fxml("/fxml/MainWindow.fxml")

    val controller: MainController by inject()

    val tfURL: JFXTextField by fxid()

    val labelStoragePath: Label by fxid()
    val labelCoreVersion: Label by fxid()
    val labelVersion: Label by fxid()

    val btnDownload: JFXButton by fxid()
    val btnChangePath: JFXButton by fxid()
    val btnUpdateCore: JFXButton by fxid()
    val btnUpdate: JFXButton by fxid()

    val paneExit: Pane by fxid()
    val apBorder: AnchorPane by fxid()

    init {
        // Window boarder
        primaryStage.initStyle(StageStyle.UNDECORATED)
        paneExit.setOnMouseClicked {
            Platform.exit()
        }

        apBorder.setOnMousePressed { event: MouseEvent? ->
            event?.let {
                xOffset = event.sceneX
                yOffset = event.sceneY
            }
        }

        apBorder.setOnMouseDragged { event: MouseEvent? ->
            event?.let {
                primaryStage.x = event.screenX - xOffset
                primaryStage.y = event.screenY - yOffset
            }
        }

        // Storage path
        labelStoragePath.text = controller.storagePath.toString()
        btnChangePath.setOnMouseClicked {
            val file = DirectoryChooser().showDialog(primaryStage)
            if (file != null) {
                config["storagePath"] = file.absolutePath.toString()
                config.save()
                labelStoragePath.text = file.absolutePath.toString()
            }
        }

        // Get media list
        btnDownload.setOnMouseClicked {
            if (tfURL.text != null && tfURL.text.trim() != "") {
                replaceWith(MediaListWindow::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                controller.requestMediaInfo(tfURL.text)
            }
        }
    }

    // clean the url textfield
    override fun onDock() {
        tfURL.text = ""
    }
}

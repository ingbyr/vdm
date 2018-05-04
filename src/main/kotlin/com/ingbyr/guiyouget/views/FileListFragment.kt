package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.models.FileItem
import com.ingbyr.guiyouget.models.FileItemModel
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXProgressBar
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import tornadofx.ListCellFragment
import tornadofx.bindTo

class FileListFragment : ListCellFragment<FileItem>() {
    override val root: AnchorPane by fxml("/fxml/FileListCell.fxml")

    private val checkBox: JFXCheckBox by fxid()
    private val labelTitle: Label by fxid()
    private val labelSize: Label by fxid()
    private val progressBar: JFXProgressBar by fxid()
    private val fileItem = FileItemModel().bindTo(this)

    init {
        checkBox.selectedProperty().bindBidirectional(fileItem.checked)
        labelTitle.textProperty().bind(fileItem.title)
        labelSize.textProperty().bind(fileItem.size)
        progressBar.progressProperty().bind(fileItem.progress)
    }
}
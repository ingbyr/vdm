package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
import com.ingbyr.guiyouget.models.FileItem
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXListView
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.View
import tornadofx.observable
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.set


class MainView : View() {
    // TODO add download playlist function
    // TODO add download with cookie
    // TODO wizard to init config and engine env: ffmpeg

    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    private val logger: Logger = LoggerFactory.getLogger(MainView::class.java)
    override val root: VBox by fxml("/fxml/MainView.fxml")
    private val controller: MainController by inject()

    private val menuNew: MenuItem by fxid()
    private val menuOpenDir: MenuItem by fxid()
    private val menuStopAll: MenuItem by fxid()
    private val menuStartAll: MenuItem by fxid()
    private val menuPreferences: MenuItem by fxid()
    private val menuQuit: MenuItem by fxid()
    private val menuAbout: MenuItem by fxid()
    private val menuHelp: MenuItem by fxid()
    private val menuCheckForUpdates: MenuItem by fxid()
    private val menuReportBug: MenuItem by fxid()
    private val menuDonate: MenuItem by fxid()
    private val btnNew: JFXButton by fxid()
    private val btnStart: JFXButton by fxid()
    private val btnStop: JFXButton by fxid()
    private val btnDelete: JFXButton by fxid()
    private val btnOpenFile: JFXButton by fxid()
    private val btnSearch: JFXButton by fxid()
    private val btnPreferences: JFXButton by fxid()
    private val listView: JFXListView<FileItem> by fxid()

    private val fileItemList = mutableListOf(FileItem(false, "test", "10MB", 0.3),
            FileItem(true, "title", "1MB", 1.0)).observable()

    init {
        listView.cellFragment(FileListFragment::class)
        listView.items = fileItemList

        btnNew.setOnMouseClicked {
            fileItemList.add(FileItem(false, LocalDateTime.now().toString(), "0MB", 0.0))
        }
    }

    private fun safeLoadConfig(key: String, defaultValue: String): String {
        return try {
            val value = app.config.string(key)
            if (value.isEmpty()) {
                throw IllegalStateException("empty value in config file")
            } else {
                return value
            }
        } catch (e: IllegalStateException) {
            app.config[key] = defaultValue
            app.config.save()
            defaultValue
        }
    }
}

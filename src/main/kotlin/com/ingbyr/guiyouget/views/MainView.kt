package com.ingbyr.guiyouget.views

import com.ingbyr.guiyouget.controllers.MainController
import com.ingbyr.guiyouget.models.DownloadTask
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXProgressBar
import javafx.geometry.Insets
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.lang.IllegalStateException
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

    init {
        val downloadTasks = mutableListOf(
                DownloadTask(false, "1", "1mb", 0.0),
                DownloadTask(false, "2", "2mb", 0.5),
                DownloadTask(false, "3", "3mb", 1.0)).observable()

        btnNew.setOnMouseClicked {
            downloadTasks.add(DownloadTask(true, "add test", "0mb", 0.2))
        }

        root += anchorpane {
            fitToParentSize()
            padding = Insets(10.0)
            tableview(downloadTasks) {
                fitToParentSize()
                columnResizePolicy = SmartResize.POLICY
                column("", DownloadTask::checkedProperty).cellFormat {
                    val cb = JFXCheckBox("")
                    cb.isSelected = it
                    graphic = cb
                }
                column(messages["ui.title"], DownloadTask::titleProperty)
                column(messages["ui.size"], DownloadTask::sizeProperty)
                column(messages["ui.progress"], DownloadTask::progressProperty).cellFormat {
                    val pb = JFXProgressBar(it.toDouble())
                    pb.useMaxSize = true
                    graphic = pb
                }
            }
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

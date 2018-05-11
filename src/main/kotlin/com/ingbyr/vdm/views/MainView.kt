package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.MainController
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.models.DownloadTask
import com.ingbyr.vdm.utils.VDMConfigUtils
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXProgressBar
import javafx.geometry.Insets
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*


class MainView : View() {
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
    private val menuDonate: MenuItem by fxid()
    private val btnNew: JFXButton by fxid()
    private val btnStart: JFXButton by fxid()
    private val btnStop: JFXButton by fxid()
    private val btnDelete: JFXButton by fxid()
    private val btnOpenFile: JFXButton by fxid()
    private val btnSearch: JFXButton by fxid()
    private val btnPreferences: JFXButton by fxid()

    private val downloadTasks = mutableListOf<DownloadTask>().observable()
    private val cu = VDMConfigUtils(app.config)

    init {
        root += anchorpane {
            fitToParentSize()
            padding = Insets(10.0)
            val downloadTaskTableView = tableview(downloadTasks) {
                fitToParentSize()
                columnResizePolicy = SmartResize.POLICY
                column("", DownloadTask::checkedProperty).cellFormat {
                    val cb = JFXCheckBox("")
                    cb.isSelected = it
                    graphic = cb
                }
                column(messages["ui.title"], DownloadTask::titleProperty)
                column(messages["ui.size"], DownloadTask::sizeProperty)
                column(messages["ui.status"], DownloadTask::progressProperty).cellFormat {
                    val pb = JFXProgressBar(it.toDouble())
                    pb.useMaxSize = true
                    graphic = pb
                }
            }

            downloadTaskTableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                logger.debug(newValue.titleProperty.value)
            }
        }

        loadVDMConfig()
        initListeners()
        subscribeEvents()
        initDownloadTaskListView()

        // TODO handle with the download task event
        // TODO save xml config file of download task
    }

    private fun loadVDMConfig() {
        // create the config file when first time use VDM
        val firstTimeUse = cu.safeLoad(VDMConfigUtils.FIRST_TIME_USE, "true").toBoolean()
        if (firstTimeUse) {
            find(PreferencesView::class).openWindow()?.hide()
            cu.update(VDMConfigUtils.FIRST_TIME_USE, "false")
            cu.saveToConfigFile()
        }
    }

    private fun initListeners() {
        // preferences view
        btnPreferences.setOnMouseClicked {
            find(PreferencesView::class).openWindow()
        }
        menuPreferences.action {
            find(PreferencesView::class).openWindow()
        }

        // create task
        btnNew.setOnMouseClicked {
            find(CreateDownloadTaskView::class).openWindow()
        }
        menuNew.action {
            find(CreateDownloadTaskView::class).openWindow()
        }

        // donate
        menuDonate.action {
            openInternalWindow(ImageView::class)
        }
    }

    private fun subscribeEvents() {
        subscribe<CreateDownloadTask> {

        }
    }

    private fun initDownloadTaskListView() {
        // TODO init downloadTasks from xml file
    }
}

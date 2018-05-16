package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.MainController
import com.ingbyr.vdm.events.CreateDownloadTask
import com.ingbyr.vdm.models.DownloadTaskModel
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

    private val downloadTaskModelList = mutableListOf<DownloadTaskModel>().observable()
    private val cu = VDMConfigUtils(app.config)


    init {
        root += anchorpane {
            fitToParentSize()
            padding = Insets(10.0)
            val downloadTaskTableView = tableview(downloadTaskModelList) {
                fitToParentSize()
                columnResizePolicy = SmartResize.POLICY
                column("", DownloadTaskModel::checkedProperty).cellFormat {
                    val cb = JFXCheckBox("")
                    cb.isSelected = it
                    graphic = cb
                }
                column(messages["ui.title"], DownloadTaskModel::titleProperty).pctWidth(50)
                column(messages["ui.size"], DownloadTaskModel::sizeProperty)
                column(messages["ui.status"], DownloadTaskModel::progressProperty).cellFormat {
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
    }

    private fun loadVDMConfig() {
        // create the config file when first time use VDM
        val firstTimeUse = cu.safeLoad(VDMConfigUtils.FIRST_TIME_USE, "true").toBoolean()
        if (firstTimeUse) {
            // init config file
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
            // TODO handle with the event
            logger.debug("create task:\n ${it.downloadTask} \n")
            downloadTaskModelList.add(DownloadTaskModel(it.downloadTask))
            // save to db
            controller.downloadTaskData.add(it.downloadTask)
        }
    }

    private fun initDownloadTaskListView() {
        controller.downloadTaskData.mapTo(downloadTaskModelList) {
            DownloadTaskModel(it)
        }
    }

    override fun onUndock() {
        super.onUndock()
        controller.clear()
    }
}

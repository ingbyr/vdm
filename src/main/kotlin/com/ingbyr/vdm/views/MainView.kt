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
import javafx.scene.control.TableView
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
    private lateinit var downloadTaskTableView: TableView<DownloadTaskModel>

    private val downloadTaskModelList = mutableListOf<DownloadTaskModel>().observable()
    private val cu = VDMConfigUtils(app.config)


    init {
        root += anchorpane {
            fitToParentSize()
            padding = Insets(10.0)
            downloadTaskTableView = tableview(downloadTaskModelList) {
                fitToParentSize()
                columnResizePolicy = SmartResize.POLICY
                column("", DownloadTaskModel::checkedProperty).cellFormat {
                    val cb = JFXCheckBox("")
                    cb.isSelected = it
                    graphic = cb
                }
                column(messages["ui.title"], DownloadTaskModel::titleProperty).pctWidth(40)
                column(messages["ui.size"], DownloadTaskModel::sizeProperty)
                column(messages["ui.status"], DownloadTaskModel::progressProperty).pctWidth(20).cellFormat {
                    val pb = JFXProgressBar(it.toDouble())
                    pb.useMaxSize = true
                    graphic = pb
                }
                column(messages["ui.createdAt"], DownloadTaskModel::createdAtProperty)
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
        // task manager
        downloadTaskTableView.selectionModel.selectedItemProperty().addListener { _, _, selectedItem ->
            // TODO handle with selected task
            val taskID = selectedItem.createdAtProperty.value
            logger.debug("select task ID: $taskID")
            logger.debug("select task info: ${controller.downloadTaskData[taskID]}")
        }

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
            logger.debug("create task:\n ${it.downloadTask} \n")
            downloadTaskModelList.add(DownloadTaskModel(it.downloadTask))
            // save to db
            controller.saveTaskToDB(it.downloadTask)
        }
    }

    private fun initDownloadTaskListView() {
        controller.downloadTaskData.mapTo(downloadTaskModelList) {
            DownloadTaskModel(it.value)
        }
    }

    override fun onUndock() {
        super.onUndock()
        controller.clear()
    }
}

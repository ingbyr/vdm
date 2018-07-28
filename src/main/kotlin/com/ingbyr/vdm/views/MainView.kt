package com.ingbyr.vdm.views

import ch.qos.logback.classic.Level
import com.ingbyr.vdm.controllers.MainController
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskStatus
import com.ingbyr.vdm.utils.VDMConfigUtils
import com.ingbyr.vdm.utils.VDMOSUtils
import com.ingbyr.vdm.utils.VDMProperties
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXProgressBar
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.text.DecimalFormat
import java.util.*


class MainView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
        title = messages["ui.vdm"]
    }

    private val vdmVersion = "0.3.1"

    private val logger: Logger = LoggerFactory.getLogger(MainView::class.java)
    override val root: VBox by fxml("/fxml/MainView.fxml")
    private val controller: MainController by inject()

    private val btnNew: JFXButton by fxid()
    private val btnStart: JFXButton by fxid()
    private val btnStop: JFXButton by fxid()
    private val btnDelete: JFXButton by fxid()
    private val btnOpenFile: JFXButton by fxid()
    private val btnSearch: JFXButton by fxid()
    private val btnPreferences: JFXButton by fxid()
    private val btnMenu: JFXButton by fxid()
    private val contextMenu: ContextMenu = ContextMenu()

    private var menuNew: MenuItem
    private var menuOpenDir: MenuItem
    private var menuStartAllTask: MenuItem
    private var menuStopAllTask: MenuItem
    private var menuPreferences: MenuItem
    private var menuAbout: MenuItem
    private var menuQuit: MenuItem
    private var menuDonate: MenuItem

    private var selectedTaskModel: DownloadTaskModel? = null
    private var downloadTaskTableView: TableView<DownloadTaskModel>

    private val cu = VDMConfigUtils(app.config)

    init {
        downloadTaskTableView = tableview(controller.downloadTaskModelList) {
            fitToParentSize()
            columnResizePolicy = SmartResize.POLICY
            // TODO multi options
//                column("", DownloadTaskModel::checkedProperty).cellFormat {
//                    val cb = JFXCheckBox("")
//                    cb.isSelected = it
//                    graphic = cb
//                }
            column(messages["ui.title"], DownloadTaskModel::titleProperty).remainingWidth()
            column(messages["ui.size"], DownloadTaskModel::sizeProperty)
            column(messages["ui.status"], DownloadTaskModel::statusProperty).cellFormat {
                val labelStatus = Label()
                when (it!!) {
                    DownloadTaskStatus.COMPLETED -> labelStatus.text = messages["ui.completed"]
                    DownloadTaskStatus.STOPPED -> labelStatus.text = messages["ui.stopped"]
                    DownloadTaskStatus.MERGING -> labelStatus.text = messages["ui.merging"]
                    DownloadTaskStatus.ANALYZING -> labelStatus.text = messages["ui.analyzing"]
                    DownloadTaskStatus.DOWNLOADING -> labelStatus.text = messages["ui.downloading"]
                    DownloadTaskStatus.FAILED -> labelStatus.text = messages["ui.failed"]
                }
                graphic = labelStatus
            }
            column(messages["ui.progress"], DownloadTaskModel::progressProperty).pctWidth(20).cellFormat {
                val progressFormat = DecimalFormat("#.##")
                val progressPane = GridPane()
                val progressBar = JFXProgressBar(it.toDouble())
                val progressLabel = Label(progressFormat.format(it.toDouble() * 100) + "%")
                progressPane.useMaxSize = true
                progressPane.add(progressBar, 0, 0)
                progressPane.add(progressLabel, 1, 0)
                val columnBar = ColumnConstraints()
                columnBar.percentWidth = 75.0
                val columnLabel = ColumnConstraints()
                columnLabel.percentWidth = 25.0
                progressPane.columnConstraints.addAll(columnBar, columnLabel)
                progressPane.hgap = 10.0
                progressBar.useMaxSize = true
                progressLabel.useMaxWidth = true
                graphic = progressPane
            }
            column(messages["ui.createdAt"], DownloadTaskModel::createdAtProperty)

            contextmenu {
                item(messages["ui.stopTask"]).action {
                    selectedTaskModel?.run { controller.stopTask(this) }
                }
                item(messages["ui.startTask"]).action {
                    selectedTaskModel?.run { controller.startTask(this) }
                }
                item(messages["ui.deleteTask"]).action {
                    selectedTaskModel?.run { controller.deleteTask(this) }
                }
            }
        }
        root += downloadTaskTableView
        downloadTaskTableView.placeholder = Label(messages["ui.noTaskInList"])

        // init context menu
        menuNew = MenuItem(messages["ui.new"])
        menuOpenDir = MenuItem(messages["ui.openDirectory"])
        menuStartAllTask = MenuItem(messages["ui.startAllTask"])
        menuStopAllTask = MenuItem(messages["ui.stopAllTask"])
        menuPreferences = MenuItem(messages["ui.preferences"])
        menuAbout = MenuItem(messages["ui.about"])
        menuQuit = MenuItem(messages["ui.quit"])
        menuDonate = MenuItem(messages["ui.donate"])
        contextMenu.items.addAll(menuNew, menuOpenDir, menuStartAllTask, menuStopAllTask, SeparatorMenuItem(), menuPreferences, menuAbout, menuDonate, SeparatorMenuItem(), menuQuit)
        loadVDMConfig()
        initListeners()
        controller.loadTaskFromDB()
    }

    private fun loadVDMConfig() {
        // create the config file when first time use VDM
        val firstTimeUse = cu.safeLoad(VDMConfigUtils.FIRST_TIME_USE, "true").toBoolean()
        if (firstTimeUse) {
            // init config file
            // TODO update version when released new one
            cu.update(VDMConfigUtils.VDM_VERSION, vdmVersion)
            cu.update(VDMConfigUtils.YOUTUBE_DL_VERSION, "2018.06.19")
            cu.update(VDMConfigUtils.YOU_GET_VERSION, VDMProperties.UNKNOWN_VERSION)

            find(PreferencesView::class).openWindow()?.hide()
            cu.update(VDMConfigUtils.FIRST_TIME_USE, "false")
        } else {
            cu.update(VDMConfigUtils.VDM_VERSION, vdmVersion)
        }
        cu.saveToConfigFile()

        // debug mode
        val rootLogger = LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        if (cu.load(VDMConfigUtils.DEBUG_MODE).toBoolean()) {
            rootLogger.level = Level.DEBUG
        } else {
            rootLogger.level = Level.ERROR
            cu.update(VDMConfigUtils.DEBUG_MODE, false)
        }
    }

    private fun initListeners() {
        // models list view
        downloadTaskTableView.selectionModel.selectedItemProperty().addListener { _, _, selectedItem ->
            selectedTaskModel = selectedItem
        }

        // shortcut buttons
        // start models
        btnStart.setOnMouseClicked {
            selectedTaskModel?.let { controller.startTask(it) }
        }
        // preferences view
        btnPreferences.setOnMouseClicked {
            find(PreferencesView::class).openWindow()
        }
        // create models
        btnNew.setOnMouseClicked {
            find(CreateDownloadTaskView::class).openWindow()
        }
        // delete models
        btnDelete.setOnMouseClicked {
            selectedTaskModel?.run { controller.deleteTask(this) }
        }
        // stop models
        btnStop.setOnMouseClicked {
            selectedTaskModel?.run { controller.stopTask(this) }
        }
        // open dir
        btnOpenFile.setOnMouseClicked {
            if (selectedTaskModel != null) {
                VDMOSUtils.openDir(selectedTaskModel!!.vdmConfig.storagePath)
            } else {
                VDMOSUtils.openDir(cu.load(VDMConfigUtils.STORAGE_PATH))
            }
        }
        // TODO search models
        btnSearch.isVisible = false
        btnSearch.setOnMouseClicked {
        }

        // menus
        btnMenu.setOnMouseClicked {
            contextMenu.show(primaryStage, it.screenX, it.screenY)
        }
        menuNew.action {
            find(CreateDownloadTaskView::class).openWindow()
        }
        menuOpenDir.action {
            if (selectedTaskModel != null) {
                VDMOSUtils.openDir(selectedTaskModel!!.vdmConfig.storagePath)
            } else {
                VDMOSUtils.openDir(cu.load(VDMConfigUtils.STORAGE_PATH))
            }
        }
        menuStartAllTask.action {
            controller.startAllTask()
        }
        menuStopAllTask.action {
            controller.stopAllTask()
        }
        menuPreferences.action {
            find(PreferencesView::class).openWindow()
        }
        menuAbout.action {
            find(AboutView::class).openWindow()
        }
        menuQuit.action {
            this.close()
        }
        menuDonate.action {
            openInternalWindow(DonationView::class)
        }
    }

    override fun onUndock() {
        super.onUndock()
        controller.clear()
        selectedTaskModel = null
    }
}
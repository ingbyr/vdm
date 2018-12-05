package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.WizardController
import com.ingbyr.vdm.utils.Attributes
import com.ingbyr.vdm.utils.ConfigUtils
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXColorPicker
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Attr
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.util.*

class WizardView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/PreferencesView")
    }

    private val logger: Logger = LoggerFactory.getLogger(WizardView::class.java)
    private val controller: WizardController by inject()
    private val summaryProperty = SimpleStringProperty()

    // bottom ui
    private val btnNext = JFXButton(messages["ui.next"])
    private val btnPrevious = JFXButton(messages["ui.previous"])
    private val labelGuide = Label()

    private val checkBoxYdl = JFXCheckBox()
    private val labelYdlVersion = Label()
    private val checkBoxAnnie = JFXCheckBox()
    private val labelAnnieVersion = Label()

    private val mediaStoragePathProperty = SimpleStringProperty(ConfigUtils.load(Attributes.STORAGE_PATH))
    private val primaryColorPicker = JFXColorPicker()
    private val secondaryColorPicker = JFXColorPicker()

    private val selectEngineView = vbox {
        paddingAll = 20.0
        spacing = 20.0
        addEngineArea(this, "youtube-dl", checkBoxYdl, labelYdlVersion)
        addEngineArea(this, "annie", checkBoxAnnie, labelAnnieVersion)
    }

    private val commonSettingView = vbox {
        // media storage path
        vbox {
            hbox {
                this += label(messages["ui.storagePath"])
                val btnChangeStoragePath = JFXButton(messages["ui.changePath"])
                btnChangeStoragePath.action {
                    val file = DirectoryChooser().showDialog(primaryStage)
                    file?.apply {
                        mediaStoragePathProperty.value = this.absoluteFile.toString()
                        controller.changeStoragePath(mediaStoragePathProperty.value)
                    }
                }
                this += btnChangeStoragePath
            }
            label().bind(mediaStoragePathProperty)
        }

        primaryColorPicker.value = c(ConfigUtils.load(Attributes.THEME_PRIMARY_COLOR))
        primaryColorPicker.setOnAction {
            controller.changePrimaryColor(primaryColorPicker.value.toString())

        }

        secondaryColorPicker.value = c(ConfigUtils.load(Attributes.THEME_SECONDARY_COLOR))
        secondaryColorPicker.setOnAction {
            controller.changeSecondaryColor(secondaryColorPicker.value.toString())
        }

        // ui
        this += hbox {
            this += label(messages["ui.primaryColor"])
            this += primaryColorPicker
        }
        this += hbox {
            this += label(messages["ui.secondaryColor"])
            this += secondaryColorPicker
        }
    }


    private val summaryView = vbox {
        label(messages["ui.displaySummaryBelow"])
        label().bind(summaryProperty)
    }

    private val steps = listOf(
        selectEngineView.toStepView("selectEngineView"),
        commonSettingView.toStepView("commonSettingView"),
        summaryView.toStepView("summaryView")
    )
    private var currentStepIndex = 0

    override val root = borderpane {
        id = "wizard-view"

        // settings area
        center {
            label()
        }

        // navigator
        bottom {
            anchorpane {
                id = "wizard-step-view"
                labelGuide.anchorpaneConstraints {
                    leftAnchor = 10.0
                    bottomAnchor = 10.0
                }
                this += labelGuide

                val nav = hbox {
                    spacing = 10.0
                    btnPrevious.buttonType = JFXButton.ButtonType.RAISED
                    btnNext.buttonType = JFXButton.ButtonType.RAISED
                    this += btnPrevious
                    this += btnNext

                    btnNext.action {
                        if (currentStepIndex < steps.lastIndex) {
                            currentStepIndex++
                            changeWizardStep(ViewTransition.Direction.LEFT)
                        } else {
                            // finish the wizard
                            controller.startDownloadSelectedEngines()
                            this@WizardView.close()
                        }
                    }

                    btnPrevious.action {
                        if (currentStepIndex > 0) {
                            currentStepIndex--
                            changeWizardStep(ViewTransition.Direction.RIGHT)
                        }
                    }
                }
                nav.anchorpaneConstraints {
                    rightAnchor = 10.0
                    bottomAnchor = 10.0
                    topAnchor = 10.0
                }
                this += nav
            }
        }

    }

    init {
        changeWizardStep(ViewTransition.Direction.LEFT)
    }

    /**
     * Add engine to [selectEngineView]
     */
    private fun addEngineArea(layout: VBox, engineName: String, cb: JFXCheckBox, labelVersion: Label) {

        cb.text = messages["ui.${engineName}"]
        cb.selectedProperty().addListener { _, _, isChecked ->
            if (isChecked) {
                controller.addEngine(cb.text)
            } else {
                controller.removeEngine(cb.text)
            }
        }
        cb.isSelected = true

        layout.add(
            vbox {
                spacing = 10.0
                this += cb
                hbox {
                    label(messages["ui.version"])
                    labelVersion
                }
                label(messages["ui.${engineName}.desc"])
            })

    }

    /**
     * Change the center view based on [currentStepIndex] and update the navigator status
     */
    private fun changeWizardStep(direction: ViewTransition.Direction) {
        root.center.replaceWith(steps[currentStepIndex].ui, ViewTransition.Slide(0.3.seconds, direction))
        labelGuide.text = steps[currentStepIndex].guide
        when (currentStepIndex) {
            0 -> {
                btnPrevious.isDisable = true
                btnNext.text = messages["ui.next"]
            }
            steps.lastIndex -> {
                btnPrevious.isDisable = false
                btnNext.text = messages["ui.finished"]

                // last view display the summaryView
                summaryProperty.value = controller.summary()
            }
            else -> {
                btnPrevious.isDisable = false
                btnNext.text = messages["ui.next"]
            }
        }

    }

    private fun VBox.toStepView(name: String) = StepView(messages["ui.${name}"], messages["ui.${name}Guide"], this)

    data class StepView(val name: String, val guide: String, val ui: VBox)

}

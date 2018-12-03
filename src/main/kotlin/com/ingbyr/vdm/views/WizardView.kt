package com.ingbyr.vdm.views

import com.ingbyr.vdm.controllers.ThemeController
import com.ingbyr.vdm.controllers.WizardController
import com.ingbyr.vdm.stylesheets.LightTheme
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXColorPicker
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.util.*

class WizardView : View() {
    init {
        messages = ResourceBundle.getBundle("i18n/WizardView")
    }

    private val logger: Logger = LoggerFactory.getLogger(WizardView::class.java)
    private val controller: WizardController by inject()

    // bottom ui
    private val btnNext = JFXButton(messages["ui.next"])
    private val btnBefore = JFXButton(messages["ui.before"])
    private val labelGuide = Label()

    private val checkBoxYdl = JFXCheckBox()
    private val labelYdlVersion = Label()
    private val checkBoxAnnie = JFXCheckBox()
    private val labelAnnieVersion = Label()

    private val colorPicker = JFXColorPicker()

    private val chooseEngines = vbox {
        paddingAll = 20.0
        spacing = 20.0
        addEngineArea(this, "youtube-dl", checkBoxYdl, labelYdlVersion)
        addEngineArea(this, "annie", checkBoxAnnie, labelAnnieVersion)
    }

    private val storageLocation = vbox {
        label("setting location")
        colorPicker.setOnAction {
            logger.debug("choose theme ${colorPicker.value}")
        }
        this += colorPicker
    }

    private val summary = vbox {
        label("apply these settings")
    }

    private val steps = listOf(
        chooseEngines.toStepView("chooseEngines"),
        storageLocation.toStepView("storageLocation"),
        summary.toStepView("summary")
    )
    private var currentStepIndex = 0

    override val root = borderpane {

        // default size
        prefWidth = 600.0
        prefHeight = 400.0

        // step list
        left {
            vbox {
                paddingAll = 20.0
                id = "main-area"
                steps.forEach { step ->
                    this += Label(step.name)
                }
            }
        }

        // settings area
        center {
            label()
        }

        // navigator
        bottom {
            anchorpane {
                id = "main-area"
                labelGuide.anchorpaneConstraints {
                    leftAnchor = 10.0
                    bottomAnchor = 10.0
                }
                this += labelGuide

                val nav = hbox {
                    this += btnBefore
                    this += btnNext

                    btnNext.action {
                        if (currentStepIndex < steps.lastIndex) {
                            currentStepIndex++
                            flushCenterView(ViewTransition.Direction.UP)
                        }
                    }

                    btnBefore.action {
                        if (currentStepIndex > 0) {
                            currentStepIndex--
                            flushCenterView(ViewTransition.Direction.DOWN)
                        }
                    }
                }
                nav.anchorpaneConstraints {
                    rightAnchor = 10.0
                    bottomAnchor = 10.0
                }
                this += nav
            }
        }

    }

    init {
        flushCenterView(ViewTransition.Direction.UP)
    }

    /**
     * Add engine to [chooseEngines]
     */
    private fun addEngineArea(layout: VBox, engineName: String, cb: JFXCheckBox, labelVersion: Label) {
        layout.add(
            vbox {
                spacing = 10.0

                cb.text = messages["ui.${engineName}"]
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
    private fun flushCenterView(direction: ViewTransition.Direction) {
        root.center.replaceWith(steps[currentStepIndex].ui, ViewTransition.Slide(0.3.seconds, direction))
        labelGuide.text = steps[currentStepIndex].guide
        when (currentStepIndex) {
            0 -> {
                btnBefore.isDisable = true
                btnNext.text = messages["ui.next"]
            }
            steps.lastIndex -> {
                btnBefore.isDisable = false
                btnNext.text = messages["ui.finished"]
            }
            else -> {
                btnBefore.isDisable = false
                btnNext.text = messages["ui.next"]
            }
        }
    }

    private fun VBox.toStepView(name: String) = StepView(messages["ui.${name}"], messages["ui.${name}Guide"], this)

    data class StepView(val name: String, val guide: String, val ui: VBox)
}

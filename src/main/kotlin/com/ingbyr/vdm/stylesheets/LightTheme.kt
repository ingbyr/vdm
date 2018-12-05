package com.ingbyr.vdm.stylesheets

import com.ingbyr.vdm.utils.Attributes
import com.ingbyr.vdm.utils.ConfigUtils
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class LightTheme : Stylesheet() {
    // main color
    private val primaryColor = c(ConfigUtils.safeLoad(Attributes.THEME_PRIMARY_COLOR, "#263238"))
    private val secondaryColor = c(ConfigUtils.safeLoad(Attributes.THEME_SECONDARY_COLOR, "#455A64"))


    companion object {
        // icon size
        private val regionSize = 1.5.em
        private val regionColor = Color.WHITE

        // id properties
        private val smallText by cssid()
        private val shortcutIcons by cssid()
        private val shortcutButton by cssid()
        private val buttonDownloadRegion by cssid()
        private val buttonPauseRegion by cssid()
        private val buttonPlayRegion by cssid()
        private val buttonDeleteRegion by cssid()
        private val buttonOpenRegion by cssid()
        private val buttonSearchRegion by cssid()
        private val buttonSettingRegion by cssid()
        private val buttonMoreMenus by cssid()
        private val labelOption by cssid()
        private val formatsListViewTitle by cssid()
        private val createDownloadTaskView by cssid()
        private val aboutView by cssid()
        private val mainView by cssid()
        private val preferencesView by cssid()
        private val mediaFormatsView by cssid()
        private val wizardView by cssid()
        private val wizardStepView by cssid()


        // class properties
        private val jfxTextField by cssclass()
        private val jfxButton by cssclass()
        private val jfxRippler by cssclass()
        private val jfxToggleButton by cssclass()
        private val jfxCheckBox by cssclass()
        private val jfxListView by cssclass()
        private val jfxProgressBar by cssclass()
        private val jfxComboBox by cssclass()
        private val jfxTabPane by cssclass()
        private val tabSelectedLine by cssclass()


        // custom jfx props
        private val jfxRipplerFill by cssproperty<Color>("-jfx-rippler-fill")
        private val jfxToogleColor by cssproperty<Color>("-jfx-toggle-color")
        private val jfxSize by cssproperty<Dimension<Dimension.LinearUnits>>("-jfx-size")
        private val jfxCheckedColor by cssproperty<Color>("-jfx-checked-color")
        private val jfxUncheckedColor by cssproperty<Color>("-jfx-unchecked-color")
        private val fxVerticalGap by cssproperty<Dimension<Dimension.LinearUnits>>("-fx-vertical-gap")
        private val jfxFocusColor by cssproperty<Color>("-jfx-focus-color")
        private val jfxUnfocusColor by cssproperty<Color>("-jfx-unfocus-color")
    }


    init {

        text {
            fontSize = 1.2.em
        }

        smallText {
            fontSize = 1.0.em
        }

        tooltip {
            fontSize = 1.0.em
            textFill = Color.WHITE
            backgroundColor += primaryColor
        }

        jfxTextField {
            fontSize = 1.0.em
            jfxFocusColor.value = primaryColor
            jfxUnfocusColor.value = secondaryColor
        }

        menu contains separator contains line {
            borderColor += box(primaryColor)
        }

        menuItem contains label {
            textFill = Color.WHITE
        }

        menuItem {
            backgroundColor += primaryColor
        }

        contextMenu {
            backgroundColor += primaryColor
        }

        menuItem and focused {
            backgroundColor += primaryColor
        }

        menuButton contains arrowButton {
            padding = box(0.0.em)
        }

        menuButton contains arrowButton contains arrow {
            padding = box(0.0.em)
        }

        // icons
        shortcutIcons {
            backgroundColor += primaryColor
        }

        shortcutButton {
            backgroundColor += primaryColor
        }

        buttonDownloadRegion {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M512 64q91 0 174 35 81 34 143 96t96 143q35 83 35 174t-35 174q-34 81-96 143t-143 96q-83 35-174 35t-174-35q-81-34-143-96T99 686q-35-83-35-174t35-174q34-81 96-143t143-96q83-35 174-35zm0-64Q373 0 255 68.5T68.5 255 0 512t68.5 257T255 955.5t257 68.5 257-68.5T955.5 769t68.5-257-68.5-257T769 68.5 512 0zm288 480H544V224q0-13-9.5-22.5T512 192t-22.5 9.5T480 224v256H224q-13 0-22.5 9.5T192 512t9.5 22.5T224 544h256v256q0 13 9.5 22.5T512 832t22.5-9.5T544 800V544h256q13 0 22.5-9.5T832 512t-9.5-22.5T800 480z"
        }

        buttonPauseRegion {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M352 768h-1q-13 0-22-9.5t-9-22.5V288q0-13 9-22.5t22-9.5h1q13 0 22.5 9.5T384 288v448q0 13-9.5 22.5T352 768zm321 0h-1q-13 0-22.5-9.5T640 736V288q0-13 9.5-22.5T672 256h1q13 0 22.5 9.5T705 288v448q0 13-9.5 22.5T673 768zM512 64q91 0 174 35 81 34 143 96t96 143q35 83 35 174t-35 174q-34 81-96 143t-143 96q-83 35-174 35t-174-35q-81-34-143-96T99 686q-35-83-35-174t35-174q34-81 96-143t143-96q83-35 174-35zm0-64Q373 0 255 68.5T68.5 255 0 512t68.5 257T255 955.5t257 68.5 257-68.5T955.5 769t68.5-257-68.5-257T769 68.5 512 0z"
        }

        buttonPlayRegion {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M512 64q91 0 174 35 81 34 143 96t96 143q35 83 35 174t-35 174q-34 81-96 143t-143 96q-83 35-174 35t-174-35q-81-34-143-96T99 686q-35-83-35-174t35-174q34-81 96-143t143-96q83-35 174-35zm0-64Q373 0 255 68.5T68.5 255 0 512t68.5 257T255 955.5t257 68.5 257-68.5T955.5 769t68.5-257-68.5-257T769 68.5 512 0zm-64 316l231 196-231 196V316zm-40-112q-9 0-16.5 6.5T384 226v572q0 9 7.5 15t17 6 16.5-6l335-285q8-7 8-16t-8-16L425 211q-7-7-17-7z"
        }

        buttonDeleteRegion {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M558 512l195-191q9-10 9.5-23t-8.5-23q-10-9-23-9t-23 9L512 467 316 275q-10-9-23-9t-23 10q-9 9-9 22.5t10 22.5l195 191-195 191q-9 10-9.5 23t8.5 23q10 9 23 9t23-9l196-192 196 192q10 9 23 9t23-10q9-9 9-22.5T753 703zM512 64q91 0 174 35 81 34 143 96t96 143q35 83 35 174t-35 174q-34 81-96 143t-143 96q-83 35-174 35t-174-35q-81-34-143-96T99 686q-35-83-35-174t35-174q34-81 96-143t143-96q83-35 174-35zm0-64Q373 0 255 68.5T68.5 255 0 512t68.5 257T255 955.5t257 68.5 257-68.5T955.5 769t68.5-257-68.5-257T769 68.5 512 0z"
        }

        buttonOpenRegion {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M405 64q5 0 11 5t7 10l19 77v1q5 23 26 39 20 17 45 17h437q10 0 10 11v597q0 5-3 8t-6 3H73q-9 0-9-11V75q0-11 9-11h332zm0-64H73Q43 0 21.5 22T0 75v746q0 31 21.5 53T73 896h878q30 0 51.5-22t21.5-53V224q0-31-21.5-53T951 149H514q-3 0-6-2.5t-4-4.5l-18-77q-7-28-30-46.5T405 0z"
        }

        buttonSearchRegion {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M1015 969L732 687q100-117 100-271 0-172-122-294T416 0 122 122 0 416t122 294 294 122q154 0 271-100l282 283q10 9 23 9t23-9q9-10 9-23t-9-23zM553 740q-65 28-137 28t-137-28q-63-26-112-75T92 553q-28-65-28-137t28-137q26-63 75-112t112-75q65-28 137-28t137 28q63 26 112 75t75 112q28 65 28 137t-28 137q-26 63-75 112t-112 75z"
        }

        buttonSettingRegion {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M512 64q26 0 54 4l16 46 10 31 32 9q32 10 62 26l29 15 29-15 43-21q44 34 78 77l-22 43-14 30 15 29q15 30 26 62l9 31 31 11 46 15q4 29 4 54v1q0 26-4 54l-46 16-31 10-10 32q-9 32-25 62l-16 29 15 29 22 43q-18 23-36 42h-1q-18 18-41 36l-43-22-29-15-29 16q-30 15-62 25l-32 10-10 31-15 46q-29 4-55 4t-55-4l-15-46-10-31-32-10q-33-10-62-25l-29-16-29 15-43 22q-23-18-42-36-18-19-36-42l22-43 15-29-16-29q-16-31-25-62l-10-32-31-10-46-15q-4-29-4-55t4-55l46-15 31-10 9-32q10-32 26-62l15-29-14-29-22-43q34-44 77-78l44 22 29 15 29-16q30-15 62-26l32-9 10-31 15-46q30-4 55-4zm0-64q-37 0-81 7l-22 4-28 83q-39 12-73 30l-78-39-18 13q-66 48-115 114l-13 18 40 78q-19 36-31 73l-83 28-3 22q-7 44-7 81t7 81l3 22 83 28q12 38 31 73l-39 78 13 18q24 34 52 62 26 26 62 52l18 13 78-39q36 19 73 30l28 84 22 3q44 7 81 7t81-7l22-3 27-84q38-11 74-30l78 39 18-13q35-26 62-52 27-27 52-62l13-18-39-79q18-35 30-73l83-27 4-22q7-44 7-81t-7-81l-4-22-83-28q-12-38-30-73l39-78-13-18q-49-67-115-115l-18-13-78 39q-35-18-73-30l-27-83-22-3q-44-7-81-7zm0 386q52 0 89 37t37 89-37 89-89 37-89-37-37-89 37-89 89-37zm0-64q-79 0-134.5 55.5T322 512t55.5 134.5T512 702t134.5-55.5T702 512t-55.5-134.5T512 322z"
        }

        buttonMoreMenus {
            backgroundColor += regionColor
            minHeight = regionSize
            minWidth = regionSize
            maxHeight = regionSize
            maxWidth = regionSize
            shape =
                    "M192 32q0-13 9.5-22.5T224 0h768q13 0 22.5 9.5T1024 32q0 14-9.5 23T992 64H224q-13 0-22.5-9T192 32zM0 32Q0 18 9.5 9T32 0t22.5 9T64 32q0 13-9.5 22.5T32 64 9.5 54.5 0 32zm192 320q0-14 9.5-23t22.5-9h768q13 0 22.5 9t9.5 23q0 13-9.5 22.5T992 384H224q-13 0-22.5-9.5T192 352zM0 350q0-13 9.5-22.5T32 318t22.5 9.5T64 350q0 14-9.5 23T32 382t-22.5-9T0 350zm192 323q0-13 9.5-22.5T224 641h768q13 0 22.5 9.5t9.5 22.5-9.5 22.5T992 705H224q-13 0-22.5-9.5T192 673zM0 672q0-13 9.5-22.5T32 640t22.5 9.5T64 672t-9.5 22.5T32 704t-22.5-9.5T0 672z"
        }

        jfxButton {
            backgroundColor += secondaryColor
            textFill = Color.WHITE
        }

        jfxButton contains jfxRippler {
            jfxRipplerFill.value = primaryColor
        }

        jfxToggleButton {
            jfxToogleColor.value = primaryColor
            jfxSize.value = 0.5.em
        }

        jfxCheckBox {
            jfxCheckedColor.value = primaryColor
            jfxUncheckedColor.value = Color.BLACK
        }

        jfxListView {
            fxVerticalGap.value = 1.0.em
        }

        jfxListView contains listCell {
            textFill = Color.BLACK
        }

        jfxListView contains listCell and selected {
            backgroundColor += secondaryColor
        }

        tableView {
            borderColor += box(primaryColor)
            backgroundInsets += box(0.em)
            padding = box(0.em)
        }

        tableRowCell {
            borderColor += box(Color.LIGHTGREY)
        }

        tableRowCell and odd {
            backgroundColor += Color.LIGHTGREY
        }

        tableRowCell and selected {
            backgroundColor += secondaryColor
        }

        tableView contains columnHeader {
            backgroundColor += secondaryColor
        }

        tableView contains columnHeader contains label {
            textFill = Color.WHITE
            fontWeight = FontWeight.BOLD
        }

        jfxProgressBar contains bar {
            backgroundColor += primaryColor
        }

        jfxComboBox {
            jfxFocusColor.value = secondaryColor
            jfxUnfocusColor.value = Color.GREY
        }

        comboBoxPopup contains listView contains listCell and filled and selected {
            backgroundColor += secondaryColor
        }

        comboBoxPopup contains listView contains listCell and filled and selected and hover {
            backgroundColor += secondaryColor
        }

        comboBoxPopup contains listView contains listCell and filled and hover {
            backgroundColor += secondaryColor
        }

        labelOption {
            textFill = Color.WHITE
        }

        tabHeaderBackground {
            backgroundColor += primaryColor
        }

        tabLabel {
            fontSize = 1.0.em
        }

        jfxTabPane contains headersRegion child tab contains jfxRippler {
            jfxRipplerFill.value = secondaryColor
        }

        jfxTabPane contains headersRegion contains tabSelectedLine {
            backgroundColor += secondaryColor
        }

        splitPane and vertical contains splitPaneDivider {
            backgroundColor += secondaryColor
            padding = box(0.0.em, 0.01.em)
        }

        formatsListViewTitle {
            backgroundColor += primaryColor
        }

        formatsListViewTitle contains label {
            textFill = Color.WHITE
        }

        createDownloadTaskView {
            prefWidth = 600.px
            prefHeight = 200.px
        }

        aboutView {
            backgroundColor += primaryColor
            prefWidth = 400.px
            prefHeight = 300.px
        }

        aboutView contains label {
            textFill = Color.WHITE
        }

        mainView {
            prefWidth = 1200.px
            prefHeight = 400.px
        }

        preferencesView {
            prefWidth = 600.px
            prefHeight = 400.px
        }

        mediaFormatsView {
            prefWidth = 500.px
            prefHeight = 500.px
        }

        wizardView {
            prefWidth = 600.px
            prefHeight = 400.px
        }
    }
}
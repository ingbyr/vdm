package com.ingbyr.vdm.stylesheets

import tornadofx.*

class DarkTheme : Stylesheet() {
    companion object {
        val mainArea by cssid()
    }

    init {
        mainArea {
            backgroundColor += c("#000000")
        }
    }
}
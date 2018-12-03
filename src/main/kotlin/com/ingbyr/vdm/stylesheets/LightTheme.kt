package com.ingbyr.vdm.stylesheets

import tornadofx.*

class LightTheme: Stylesheet() {
    companion object {
        val mainArea by cssid()
    }

    init {
        mainArea {
            backgroundColor += c("#dddddd")
        }
    }
}
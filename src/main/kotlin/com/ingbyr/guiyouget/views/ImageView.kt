package com.ingbyr.guiyouget.views

import tornadofx.View
import tornadofx.imageview
import tornadofx.vbox

class ImageView : View() {
    override val root = vbox {
        imageview("/img/zhifubao_compress.jpg", lazyload = false)
    }
}

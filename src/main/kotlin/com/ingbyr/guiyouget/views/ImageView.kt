package com.ingbyr.guiyouget.views

import tornadofx.*

class ImageView : View() {
    override val root = vbox  {
        imageview("/img/zhifubao_compress.jpg", lazyload = false)
    }
}

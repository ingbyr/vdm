package com.ingbyr.vdm.views

import com.ingbyr.vdm.utils.AppProperties
import tornadofx.*


class DonationView : View() {
    // TODO hidpi size
    override val root = hbox {
        spacing = 10.0
        imageview("/imgs/zhifubao-min.png")
        imageview("/imgs/paypal-min.png").setOnMouseClicked {
            hostServices.showDocument(AppProperties.DONATION_URL)
        }
    }
}

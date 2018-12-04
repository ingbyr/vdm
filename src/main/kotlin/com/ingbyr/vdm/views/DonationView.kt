package com.ingbyr.vdm.views

import com.ingbyr.vdm.utils.Attributes
import tornadofx.*


class DonationView : View() {
    override val root = hbox {
        spacing = 10.0
        imageview("/imgs/zhifubao-min.png")
        imageview("/imgs/paypal-min.png").setOnMouseClicked {
            hostServices.showDocument(Attributes.DONATION_URL)
        }
    }
}

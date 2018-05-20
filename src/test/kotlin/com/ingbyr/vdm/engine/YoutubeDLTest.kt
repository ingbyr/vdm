package com.ingbyr.vdm.engine

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class YoutubeDLTest : FunSpec({
    test("check new updates") {
        YoutubeDL().existNewVersion("2018.05.18") shouldBe false
        YoutubeDL().existNewVersion("2018.03.12") shouldBe true
        YoutubeDL().existNewVersion("2018.05.1") shouldBe true
    }
})
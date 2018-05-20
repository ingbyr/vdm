package com.ingbyr.vdm.utils

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class VDMUtilsTests : FunSpec({
    test("compare local and remote version") {
        VDMUtils.newVersion("1.0", "1.1") shouldBe true
        VDMUtils.newVersion("1.0", "1.0.1") shouldBe true
        VDMUtils.newVersion("1.0.1", "1.1.1") shouldBe true
        VDMUtils.newVersion("1.0", "1.0") shouldBe false
        VDMUtils.newVersion("1.0", "0.9.1") shouldBe false
        VDMUtils.newVersion("1.0", "0.9") shouldBe false
        VDMUtils.newVersion("1.0", "1.0") shouldBe false
        VDMUtils.newVersion("1.0", "1.0.0") shouldBe false
        VDMUtils.newVersion("1.0.0", "1.0") shouldBe false
        VDMUtils.newVersion("1.0.0", "1.0.0") shouldBe false
    }
})
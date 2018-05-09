package com.ingbyr.vdm.models

import com.ingbyr.vdm.utils.EngineType
import com.ingbyr.vdm.utils.ProxyType

data class VDMConfig(var engineType: EngineType, var url: String, var proxyType: ProxyType, var address: String,
                     var port: String, var output: String, var formatID: String)


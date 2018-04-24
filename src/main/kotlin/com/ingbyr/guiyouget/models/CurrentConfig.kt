package com.ingbyr.guiyouget.models

import com.ingbyr.guiyouget.utils.EngineType
import com.ingbyr.guiyouget.utils.ProxyType

data class CurrentConfig(var engineType: EngineType, var url: String, var proxyType: ProxyType, var address: String,
                         var port: String, var output: String, var formatID: String)


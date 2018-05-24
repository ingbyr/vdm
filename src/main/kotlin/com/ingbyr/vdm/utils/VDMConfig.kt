package com.ingbyr.vdm.utils

import com.ingbyr.vdm.engine.utils.EngineType
import java.io.Serializable

/**
 * Instance for the config file
 */
data class VDMConfig(val engineType: EngineType, val proxy: VDMProxy, val downloadDefaultFormat: Boolean, val storagePath: String, val cookieString: String = "", val FFMPEGPath: String = "") : Serializable
package com.ingbyr.vdm.utils.config

import com.ingbyr.vdm.engines.utils.EngineType
import com.ingbyr.vdm.models.ProxyType
import com.ingbyr.vdm.utils.Attributes
import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*

/**
 * Extension app helper
 */
fun ConfigProperties.engine(key: String, defaultValue: EngineType): EngineType {
    return try {
        EngineType.valueOf(this.string(key).toString())
    } catch (e: IllegalArgumentException) {
        val logger: Logger = LoggerFactory.getLogger(Attributes.javaClass)
        logger.error("not fount engine type: ${this.string(key).toString()}")
        this.update(key, defaultValue.name)
        defaultValue
    }
}

fun ConfigProperties.proxy(key: String, defaultValue: ProxyType = ProxyType.NONE): ProxyType {
    return try {
        ProxyType.valueOf(this.string(key).toString())
    } catch (e: IllegalArgumentException) {
        val logger: Logger = LoggerFactory.getLogger(Attributes.javaClass)
        logger.error("not fount proxy type: ${this.string(key).toString()}")
        this.update(key, defaultValue.name)
        defaultValue
    }
}

fun ConfigProperties.color(key: String, defaultValue: Color = Color.RED): Color {
    val color = this.string(key)
    return if (color != null) {
        c(color)
    } else {
        defaultValue
    }
}

fun ConfigProperties.update(k: String, v: Any) {
    with(this) {
        set(k, v.toString())
        save()
    }
}


// Config patcher because that no app in some class
object app {

    object config : Controller() {
        fun color(key: String, defaultValue: Color = Color.RED): Color = config.color(key, defaultValue)
        fun string(key: String, defaultValue: String) = config.string(key, defaultValue)
    }

}



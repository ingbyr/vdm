package com.ingbyr.vdm.utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ingbyr.vdm.engines.utils.EngineInfo
import org.slf4j.LoggerFactory


object EnginesJsonUtils {
    private val logger = LoggerFactory.getLogger(EnginesJsonUtils::class.java)
    private val enginesJson = jacksonObjectMapper().readValue<EnginesJson>(
            AppProperties.PACKAGE_DIR.resolve("engines.json").toFile().readText())

    val engines = when (OSUtils.currentOS) {
        OSType.WINDOWS -> enginesJson.windows
        OSType.LINUX -> enginesJson.linux
        OSType.MAC_OS -> enginesJson.macos
    }

    /*
    engineName should be same with name in engines.json
     */
    fun engineInfo(engineName: String): EngineInfo {
        engines.forEach {
            if (it.name == engineName) return it
        }
        throw EngineInfoException("not found engine info of $engineName")
    }

    fun save2JsonFile() {
        logger.debug(enginesJson.toString())
        val dataString = jacksonObjectMapper().writeValueAsString(enginesJson)
        logger.debug(dataString)
        AppProperties.PACKAGE_DIR.resolve("engines.json").toFile().writeText(dataString)
    }
}

data class EnginesJson(
        @JsonProperty("windows") val windows: List<EngineInfo>,
        @JsonProperty("linux") val linux: List<EngineInfo>,
        @JsonProperty("macos") val macos: List<EngineInfo>
)

class EngineInfoException(override var message: String) : Exception(message)
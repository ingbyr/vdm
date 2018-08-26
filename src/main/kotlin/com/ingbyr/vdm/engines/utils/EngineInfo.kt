package com.ingbyr.vdm.engines.utils

import com.fasterxml.jackson.annotation.JsonProperty

data class EngineInfo(
        @JsonProperty("name") val name: String,
        @JsonProperty("path") val path: String,
        @JsonProperty("version") var version: String,
        @JsonProperty("remoteVersionUrl") val remoteVersionUrl: String
)
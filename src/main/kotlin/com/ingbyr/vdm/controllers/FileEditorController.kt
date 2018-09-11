package com.ingbyr.vdm.controllers

import com.ingbyr.vdm.utils.AppProperties
import java.nio.file.Path

class FileEditorController : ConfigController() {
    fun saveFile(filePath: Path, fileContent: String) {
        filePath.toFile().writeText(fileContent)
        updateConfig(AppProperties.CURRENT_COOKIE, filePath.fileName.toString())
    }
}
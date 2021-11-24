package com.ingbyr.vdm.utils

import java.nio.file.Path

data class FileEditorOption(
        val filePath: Path,
        val isNewFile: Boolean,
        val extension: String
)
package com.ingbyr.guiyouget.models

class Media(formatR: String?, formatNoteR: String?, fileSizeR: Int?, formatIDR: String?, extR: String?) {
    val format: String = formatR ?: "None"
    val formatNote: String = formatNoteR ?: "None"
    val size: Int = (fileSizeR ?: 0) / 1000000
    val formatID: String = formatIDR ?: "None"
    val ext: String = extR ?: ""
}
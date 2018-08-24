package com.ingbyr.vdm.models

data class MediaFormat(
        val title: String,
        val desc: String,
        val formatID: String,
        val format: String,
        val formatNote: String,
        val fileSize: Long,
        val ext: String)
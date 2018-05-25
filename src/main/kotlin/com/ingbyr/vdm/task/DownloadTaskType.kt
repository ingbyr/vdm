package com.ingbyr.vdm.task

import java.io.Serializable

enum class DownloadTaskType : Serializable {
    SINGLE_MEDIA,
    PLAYLIST,
    ENGINE
}
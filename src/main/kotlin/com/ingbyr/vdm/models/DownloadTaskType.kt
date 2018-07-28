package com.ingbyr.vdm.models

import java.io.Serializable

enum class DownloadTaskType : Serializable {
    SINGLE_MEDIA,
    PLAYLIST,
    ENGINE
}
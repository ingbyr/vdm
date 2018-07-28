package com.ingbyr.vdm.models

import java.io.Serializable

enum class DownloadTaskStatus : Serializable {
    ANALYZING,
    DOWNLOADING,
    STOPPED,
    FAILED,
    COMPLETED,
    MERGING
}
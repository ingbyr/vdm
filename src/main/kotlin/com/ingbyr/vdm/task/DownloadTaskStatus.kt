package com.ingbyr.vdm.task

import java.io.Serializable

enum class DownloadTaskStatus : Serializable {
    ANALYZING,
    DOWNLOADING,
    STOPPED,
    FAILED,
    COMPLETED,
    MERGING
}
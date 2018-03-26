package com.ingbyr.guiyouget.utils

enum class EngineStatus {
    /**
     * Different download engine status
     */
    ANALYZE,
    DOWNLOAD,
    PAUSE,
    RESUME,
    FAIL
}

enum class DownloadType {
    JSON,
    SINGLE,
    PLAYLIST,
    OTHERS
}

object EngineUtils {
    const val DOWNLOAD_CORE = "download-engine"

    const val YOUTUBE_DL = "youtube-dl"
    const val YOUTUBE_DL_VERSION = "youtube-dl-version"
    const val REMOTE_YOUTUBE_DL_VERSION = "https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py"

    const val YOU_GET = "you-get"
    const val YOU_GET_VERSION = "you-get-version"
    const val REMOTE_YOU_GET_VERSION = "https://raw.githubusercontent.com/soimort/you-get/master/src/you_get/version.py"
}
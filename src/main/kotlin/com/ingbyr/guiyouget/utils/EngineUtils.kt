package com.ingbyr.guiyouget.utils

enum class EngineType {
    ENGINE_TYPE,
    YOUTUBE_DL,
    YOU_GET,
    NONE
}

enum class EngineStatus {
    /**
     * Different download engine status
     */
    ANALYZE,
    DOWNLOAD,
    PAUSE,
    RESUME,
    FAIL,
    FINISH
}

enum class DownloadType {
    JSON,
    SINGLE,
    PLAYLIST
}

object EngineUtils {
    const val YOUTUBE_DL_VERSION = "YOUTUBE_DL_VERSION"
    const val REMOTE_YOUTUBE_DL_VERSION = "https://raw.githubusercontent.com/rg3/youtube-dl/master/youtube_dl/version.py"
    const val YOU_GET_VERSION = "YOU_GET_VERSION"
    const val REMOTE_YOU_GET_VERSION = "https://raw.githubusercontent.com/soimort/you-get/master/src/you_get/version.py"

    fun yougetUpdateURL(ver: String) = "https://github.com/soimort/you-get/releases/download/v$ver/you-get-$ver-win32.exe"
    fun youtubedlUpdateURL(ver: String) = "https://github.com/rg3/youtube-dl/releases/download/$ver/youtube-dl.exe"
}

class DownloadEngineException(message: String) : Exception(message)
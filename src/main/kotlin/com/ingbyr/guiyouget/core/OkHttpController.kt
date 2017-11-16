package com.ingbyr.guiyouget.core

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.events.UpdateYouGetStates
import com.ingbyr.guiyouget.events.UpdateYoutubeDLStates
import com.ingbyr.guiyouget.utils.CoreUtils
import okhttp3.*
import okio.Okio
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.IOException
import java.nio.file.Paths
import java.util.*


class OkHttpController : Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val client = OkHttpClient()
    private val parser = Parser()

    init {
        messages = ResourceBundle.getBundle("i18n/core")
    }

    fun requestString(url: String): String? {
        val request = Request.Builder().get().url(url).build()
        var msg: String? = null
        client.newCall(request).execute().let { response ->
            msg = response.body()?.string()
        }
        return msg
    }

    fun requestJson(url: String): JsonObject? {
        val request = Request.Builder().get().url(url).build()
        var msg: String? = null
        client.newCall(request).execute().let { response ->
            msg = response.body()?.string()
        }
        if (msg != null) {
            return parser.parse(StringBuilder(msg)) as JsonObject
        } else {
            return null
        }
    }

    fun downloadFile(url: String, file: String, k: String? = null, v: String? = null) {
        val request = Request.Builder().url(url).build()
        logger.debug("file path is $file")
        try {
            client.newCall(request).enqueue(DownloadFileCallBack(file, k, v))
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }
}

class DownloadFileCallBack(private val file: String, private val k: String?, private val v: String?) : Callback, Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/core")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun onFailure(call: Call?, e: IOException?) {
        when (k) {
            CoreUtils.YOUTUBE_DL_VERSION -> {
                logger.debug("failed to update youtube-dl")
                fire(UpdateYoutubeDLStates(messages["failed"]))
            }
            CoreUtils.YOU_GET_VERSION -> {
                logger.debug("failed to update you-get")
                fire(UpdateYouGetStates(messages["failed"]))
            }
        }
        logger.error(e.toString())
    }

    override fun onResponse(call: Call?, response: Response) {
        if (response.isSuccessful) {
            val sink = Okio.buffer(Okio.sink(Paths.get(file).toFile()))
            sink.writeAll(response.body()!!.source())
            sink.close()
            response.close()

            // Update config of APP
            if (k != null && v != null) {
                app.config[k] = v
                app.config.save()
            }

            when (k) {
                CoreUtils.YOUTUBE_DL_VERSION -> {
                    fire(UpdateYoutubeDLStates(messages["completed"]))
                }
                CoreUtils.YOU_GET_VERSION -> {
                    fire(UpdateYouGetStates(messages["completed"]))
                }
            }
        } else {
            logger.error("bad request")
            response.close()
            when (k) {
                CoreUtils.YOUTUBE_DL_VERSION -> {
                    logger.debug("failed to update youtube-dl")
                    fire(UpdateYoutubeDLStates(messages["failed"]))
                }
                CoreUtils.YOU_GET_VERSION -> {
                    logger.debug("failed to update you-get")
                    fire(UpdateYouGetStates(messages["failed"]))
                }
            }
        }
    }
}
package com.ingbyr.guiyouget.core

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ingbyr.guiyouget.events.UpdateStates
import okhttp3.*
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class OkHttpController : Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val client = OkHttpClient()
    private val parser = Parser()

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

    fun downloadFile(url: String, file: File, k: String? = null, v: String? = null) {
        val request = Request.Builder().url(url).build()
        logger.debug("save file to ${file.absolutePath}")
        client.newCall(request).enqueue(DownloadFileCallBack(file, k, v))
    }
}

class DownloadFileCallBack(private val file: File, private val k: String?, private val v: String?) : Callback, Controller() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun onFailure(call: Call?, e: IOException?) {
        fire(UpdateStates("Updating failed"))
        logger.error(e.toString())
    }

    override fun onResponse(call: Call?, response: Response) {
        if (!response.isSuccessful) throw IOException("Unexpected code " + response)
        val byteStream = response.body()?.byteStream()
        val length = response.body()?.contentLength()
        logger.debug("length $length")
        val os: FileOutputStream
        try {
            os = FileOutputStream(file)
        } catch (e: Exception) {
            logger.error(e.toString())
            return
        }

        var bytesRead = -1
        val buffer = ByteArray(2048)
        var process = 0L
        try {
            do {
                if (byteStream != null) {
                    bytesRead = byteStream.read(buffer)
                }
                if (bytesRead == -1) break
                process += bytesRead
                logger.trace(process.toString())
                os.write(buffer, 0, bytesRead)
            } while (true)
        } catch (e: Exception) {
            logger.error(e.toString())

        }
        fire(UpdateStates("Complete updates"))
        // Update config of APP
        if (k != null && v != null) {
            app.config[k] = v
            app.config.save()
        }
    }
}
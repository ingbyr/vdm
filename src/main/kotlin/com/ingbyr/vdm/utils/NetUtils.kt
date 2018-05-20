package com.ingbyr.vdm.utils

import javafx.beans.property.SimpleLongProperty
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.Util
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException


object NetUtils {
    private val client = OkHttpClient()
    private val logger: Logger = LoggerFactory.getLogger(NetUtils::class.java)

    fun get(url: String): String {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body()?.string() ?: "No response"
    }

    fun download(url: String, destFile: File, progress: SimpleLongProperty) {
        var sink: BufferedSink? = null
        var source: BufferedSource? = null
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body()
            if (body != null) {
                val contentLength = body.contentLength()
                source = body.source()
                sink = Okio.buffer(Okio.sink(destFile))
                val sinkBuffer = sink.buffer()
                var totalBytesRead: Long = 0
                val bufferSize: Long = 2 * 1024
                var bytesRead: Long = 0

                while (true) {
                    bytesRead = source.read(sinkBuffer, bufferSize)
                    if (bytesRead == -1L) break
                    sink.emit()
                    totalBytesRead += bytesRead
                    progress.value = totalBytesRead * 100 / contentLength
                    logger.trace("download $url progress: $progress")
                }
                sink.flush()
            } else {
                logger.error("no response body")
            }
        } catch (e: IOException) {
            logger.error(e.toString())
        } finally {
            Util.closeQuietly(sink)
            Util.closeQuietly(source)
        }
    }
}
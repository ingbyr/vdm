package com.ingbyr.vdm.utils

import com.ingbyr.vdm.models.DownloadTaskModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.Util
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.IOException
import java.nio.file.Paths
import java.util.*


object NetUtils {
    private val client = OkHttpClient()
    private val logger: Logger = LoggerFactory.getLogger(NetUtils::class.java)

    fun get(url: String): String? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body()?.string()
    }

    fun download(downloadTaskModel: DownloadTaskModel, ui: ResourceBundle) {
        var sink: BufferedSink? = null
        var source: BufferedSource? = null
        downloadTaskModel.status = ui["ui.analyzing"]
        try {
            val request = Request.Builder().url(downloadTaskModel.url).build()
            val response = client.newCall(request).execute()
            val body = response.body()
            if (body != null) {
                downloadTaskModel.status = ui["ui.downloading"]
                val contentLength = body.contentLength()
                downloadTaskModel.size = "${contentLength / 1000000.0}Mb" // TODO not accurate
                source = body.source()
                sink = Okio.buffer(Okio.sink(Paths.get(downloadTaskModel.vdmConfig.storagePath)))
                val sinkBuffer = sink.buffer()
                var totalBytesRead: Long = 0
                val bufferSize: Long = 2 * 1024
                var bytesRead: Long = 0

                while (true) {
                    bytesRead = source.read(sinkBuffer, bufferSize)
                    if (bytesRead == -1L) break
                    sink.emit()
                    totalBytesRead += bytesRead
                    downloadTaskModel.progress = totalBytesRead * 100.0 / contentLength
                }
                sink.flush()
                downloadTaskModel.status = ui["ui.completed"]
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
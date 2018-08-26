package com.ingbyr.vdm.utils

import com.ingbyr.vdm.events.RefreshEngineVersion
import com.ingbyr.vdm.models.DownloadTaskModel
import com.ingbyr.vdm.models.DownloadTaskStatus
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.Util
import okio.BufferedSource
import okio.Okio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission
import java.text.DecimalFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import okio.BufferedSink
import okio.Sink
import java.io.File


class NetUtils : Controller() {
    init {
        messages = ResourceBundle.getBundle("i18n/MainView")
    }

    private val client = OkHttpClient()
    private val logger: Logger = LoggerFactory.getLogger(NetUtils::class.java)

    fun get(url: String): String? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body()?.string()
    }

    fun downloadEngine(downloadTaskModel: DownloadTaskModel,
                       remoteVersion: String,
                       needUnzip: Boolean = false) {
        downloadTaskModel.status = DownloadTaskStatus.ANALYZING
        try {
            val request = Request.Builder().url(downloadTaskModel.taskConfig.url).build()
            val response = client.newCall(request).execute()
            val body = response.body()
            if (body != null) {
                downloadTaskModel.status = DownloadTaskStatus.DOWNLOADING
                val contentLength = body.contentLength()
                val sizeFormat = DecimalFormat("#.##")
                downloadTaskModel.size = "${sizeFormat.format(contentLength / 1000000.0)}MB"
                val storagePath = Paths.get(downloadTaskModel.taskConfig.storagePath)

                if (needUnzip) {
                    val tmpFile: Path = AppProperties.USER_DIR.resolve("tmp.zip")
                    saveBufferData(body.source(), tmpFile, downloadTaskModel, contentLength)
                    // unzip tmp file and clear it
                    unzipFile(tmpFile, storagePath)
                    Files.delete(tmpFile)
                } else {
                    saveBufferData(body.source(), storagePath, downloadTaskModel, contentLength)
                }

                downloadTaskModel.status = DownloadTaskStatus.COMPLETED

                // add execution permission to the engines file
                if (OSUtils.currentOS == OSType.LINUX || OSUtils.currentOS == OSType.MAC_OS) {
                    Files.setPosixFilePermissions(
                            storagePath,
                            mutableSetOf(
                                    PosixFilePermission.OWNER_READ,
                                    PosixFilePermission.OWNER_WRITE,
                                    PosixFilePermission.OWNER_EXECUTE,
                                    PosixFilePermission.GROUP_EXECUTE,
                                    PosixFilePermission.OTHERS_EXECUTE))
                }
                // update ui
                fire(RefreshEngineVersion(downloadTaskModel.taskConfig.engineType, remoteVersion))
            } else {
                logger.error("no response body")
            }
        } catch (e: IOException) {
            downloadTaskModel.status = DownloadTaskStatus.FAILED
            logger.error(e.toString())
        }
    }

    private fun saveBufferData(source: BufferedSource, dest: Path,
                               downloadTaskModel: DownloadTaskModel, contentLength: Long) {
        logger.debug("saving data to $dest")

        Okio.buffer(Okio.sink(dest)).use { sink->
            source.use { source ->
                val sinkBuffer = sink.buffer()
                var totalBytesRead: Long = 0
                val bufferSize: Long = 2 * 1024
                var bytesRead: Long
                while (true) {
                    bytesRead = source.read(sinkBuffer, bufferSize)
                    if (bytesRead == -1L) break
                    sink.emit()
                    totalBytesRead += bytesRead
                    downloadTaskModel.progress = totalBytesRead.toDouble() / contentLength.toDouble()
                }
                sink.flush()
            }
        }
    }

    private fun unzipFile(source: Path, dest: Path) {
        // unzip file
        logger.debug("unzip $source to $dest")
        ZipFile(source.toFile()).use { zipFile ->
            val enu = zipFile.entries()
            while (enu.hasMoreElements()) {
                val zipEntry: ZipEntry = enu.nextElement()
                zipFile.getInputStream(zipEntry).use {
                    Files.write(dest, it.readBytes())
                }
            }
        }
    }
}
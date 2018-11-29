package com.ingbyr.vdm.utils

import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

object FileCompressUtils {
    private val log: Logger = LoggerFactory.getLogger(FileCompressUtils::class.java)

    fun decompress(sourceFile: File, destFile: File) {
        val fileExtension = FilenameUtils.getExtension(sourceFile.name)
        log.debug("decompress the ${sourceFile.name} as $fileExtension")
        when (fileExtension) {
            "gz" -> decompressGz(sourceFile, destFile)
            else -> decompressUsualFile(sourceFile, destFile, fileExtension)
        }
    }


    /**
     * Decompress tar.gz file. tar.gz -> tar -> decompressed file
     */
    private fun decompressGz(sourceFile: File, destFile: File) {
        val tmpTarFile = AppProperties.TMP_DIR.resolve("tmp.tar").toFile()
        GzipCompressorInputStream(sourceFile.inputStream().buffered()).use { gis ->
            IOUtils.copy(gis, tmpTarFile.outputStream())
        }

        decompressUsualFile(tmpTarFile, destFile, ArchiveStreamFactory.TAR)
        Files.delete(tmpTarFile.toPath())
    }

    /**
     * Decompress usual compressed files.
     */
    private fun decompressUsualFile(sourceFile: File, destFile: File, fileType: String? = null) {
        if (fileType.isNullOrBlank()) { // try to decompress unknown file
            ArchiveStreamFactory().createArchiveInputStream(sourceFile.inputStream()).use { ais ->
                readArchiveStreamAndSave(ais, destFile)
            }
        } else {
            ArchiveStreamFactory().createArchiveInputStream(fileType, sourceFile.inputStream()).use { ais ->
                readArchiveStreamAndSave(ais, destFile)
            }
        }
    }

    private fun readArchiveStreamAndSave(ais: ArchiveInputStream, destFile: File) {
        val entry = ais.nextEntry
        log.debug("decompress ${entry.name} (size: ${entry.size})")
        IOUtils.copy(ais, destFile.outputStream())
    }
}
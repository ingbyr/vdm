package com.ingbyr.vdm.utils

import org.junit.jupiter.api.Test


class FileCompressUtilsTests {
    @Test
    fun `decompress gz file`() {
        val sf = Attributes.TMP_DIR.resolve("decompress_gz_file.tar.gz").toFile()
        val df = Attributes.TMP_DIR.resolve("decompress_gz_file").toFile()
        FileCompressUtils.decompress(sf, df)
    }

    @Test
    fun `decompress zip file`() {
        val sf = Attributes.TMP_DIR.resolve("decompress_gz_file.zip").toFile()
        val df = Attributes.TMP_DIR.resolve("decompress_gz_file").toFile()
        FileCompressUtils.decompress(sf, df)
    }
}
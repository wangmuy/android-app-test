package com.example.test.mainbiz.util

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

object TarExtractor {
    fun extract(inputStream: InputStream, destDir: File) {
        val bufferedStream = BufferedInputStream(inputStream)
        GzipCompressorInputStream(bufferedStream).use { gzipIn ->
            TarArchiveInputStream(gzipIn).use { tarInput ->
                var entry = tarInput.nextTarEntry
                while (entry != null) {
                    val file = File(destDir, entry.name)

                    if (entry.isSymbolicLink) {
                        val linkPath = file.toPath()
                        val targetPath = Paths.get(entry.linkName)
                        Files.deleteIfExists(linkPath)
                        Files.createSymbolicLink(linkPath, targetPath)
                    } else if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { output ->
                            tarInput.copyTo(output)
                        }
                    }

                    entry = tarInput.nextTarEntry
                }
            }
        }
    }
}

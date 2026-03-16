package com.example.test.mainbiz.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object ScriptExtractor {
    private const val SCRIPT_NAME = "appfunc"

    fun extractScript(context: Context): File? {
        val destFile = File(context.filesDir, SCRIPT_NAME)

        if (destFile.exists()) {
            return destFile
        }

        return try {
            context.assets.open(SCRIPT_NAME).use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destFile.setExecutable(true)
            destFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getScriptPath(context: Context): String {
        return File(context.filesDir, SCRIPT_NAME).absolutePath
    }
}

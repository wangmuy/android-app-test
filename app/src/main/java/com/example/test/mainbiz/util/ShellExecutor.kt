package com.example.test.mainbiz.util

import android.content.Context
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ShellExecutor(
    private val context: Context,
    private val scriptDir: String,
    private val onOutput: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onCommandDone: (Int) -> Unit
) {
    private var process: Process? = null
    private var stdoutReader: BufferedReader? = null
    private var stderrReader: BufferedReader? = null
    private var stdinWriter: BufferedWriter? = null
    private var stdoutThread: Thread? = null
    private var stderrThread: Thread? = null

    private val prootFile: File
        get() = File(context.filesDir, PROOT_NAME)

    private val alpineDir: File
        get() = File(context.filesDir, ALPINE_DIR)

    private val prootTmpDir: File
        get() = File(context.filesDir, PROOT_TMP_DIR)

    fun startShell(): Boolean {
        return try {
            extractProot()
            extractAlpineRootfs()
            ensureProotTmpDir()

            val alpinePath = alpineDir.absolutePath
            val prootTmpPath = prootTmpDir.absolutePath

            val processBuilder = ProcessBuilder()
                .redirectErrorStream(false)

            val env = processBuilder.environment()
            env["PATH"] = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
            env["HOME"] = "/root"
            env["PROOT_TMP_DIR"] = prootTmpPath
            env.remove("LD_PRELOAD")

            /**
             * if sh command gives:
             *
             * 1. Clear the broken links first: From outside PRoot (in your Android app's terminal), delete the /bin directory and recreate it to clear the "bad" links
             * ```
             * rm -rf /data/user/0/com.example.test/files/alpine/bin
             * mkdir -p /data/user/0/com.example.test/files/alpine/bin
             * # Put ONLY the busybox binary back
             * cp /path/to/original/busybox /data/user/0/com.example.test/files/alpine/bin/busybox
             * chmod 755 /data/user/0/com.example.test/files/alpine/bin/busybox
             * ```
             *
             * 2. Start PRoot with `--link2symlink`
             *
             * 3. Install links from INSIDE the protected PRoot session
             * `/bin/busybox --install -s /bin`
             */
            processBuilder.command(
                prootFile.absolutePath,
                "--link2symlink",
                "-r", alpinePath,
                "-0",
                "-w", "/root",
                "-b", "/dev",
                "-b", "/proc",
                "-b", "/sys",
//                "-v","9",
                "/bin/busybox", "sh"
            )
            processBuilder.directory(context.filesDir)

            process = processBuilder.start()

            stdoutReader = BufferedReader(InputStreamReader(process!!.inputStream))
            stderrReader = BufferedReader(InputStreamReader(process!!.errorStream))
            stdinWriter = BufferedWriter(OutputStreamWriter(process!!.outputStream))

            stdoutThread = Thread {
                try {
                    var line: String?
                    while (stdoutReader?.readLine().also { line = it } != null) {
                        line?.let {
                            if (it.startsWith("${SENTINEL_PREFIX}_FINISHED:")) {
                                val exitCode = it.substringAfterLast(":").trim().toIntOrNull() ?: 0
                                runBlocking {
                                    onCommandDone(exitCode)
                                }
                            } else {
                                onOutput(it)
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }

            stderrThread = Thread {
                try {
                    var line: String?
                    while (stderrReader?.readLine().also { line = it } != null) {
                        line?.let { onError(it) }
                    }
                } catch (e: Exception) {
                }
            }

            stdoutThread?.start()
            stderrThread?.start()

            Thread {
                process?.waitFor()
                runBlocking {
                    onError("[shell exited with code ${process?.exitValue() ?: -1}]")
                }
            }.start()

            true
        } catch (e: Exception) {
            onError("Failed to start proot: ${e.message}, falling back to native sh. stack=${e.stackTraceToString()}")
            startNativeShell()
        }
    }

    private fun startNativeShell(): Boolean {
        return try {
            val processBuilder = ProcessBuilder("sh")
                .redirectErrorStream(false)

            val env = processBuilder.environment()
            env["PATH"] = "$scriptDir:${env["PATH"]}"

            process = processBuilder.start()

            stdoutReader = BufferedReader(InputStreamReader(process!!.inputStream))
            stderrReader = BufferedReader(InputStreamReader(process!!.errorStream))
            stdinWriter = BufferedWriter(OutputStreamWriter(process!!.outputStream))

            stdoutThread = Thread {
                try {
                    var line: String?
                    while (stdoutReader?.readLine().also { line = it } != null) {
                        line?.let {
                            if (it.startsWith("${SENTINEL_PREFIX}_FINISHED:")) {
                                val exitCode = it.substringAfterLast(":").trim().toIntOrNull() ?: 0
                                runBlocking {
                                    onCommandDone(exitCode)
                                }
                            } else {
                                onOutput(it)
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }

            stderrThread = Thread {
                try {
                    var line: String?
                    while (stderrReader?.readLine().also { line = it } != null) {
                        line?.let { onError(it) }
                    }
                } catch (e: Exception) {
                }
            }

            stdoutThread?.start()
            stderrThread?.start()

            Thread {
                process?.waitFor()
                runBlocking {
                    onError("[shell exited with code ${process?.exitValue() ?: -1}]")
                }
            }.start()

            true
        } catch (e: Exception) {
            onError("Failed to start native shell: ${e.message}")
            false
        }
    }

    private fun extractProot() {
        if (!prootFile.exists()) {
            context.assets.open(PROOT_NAME).use { input ->
                FileOutputStream(prootFile).use { output ->
                    input.copyTo(output)
                }
            }
            prootFile.setExecutable(true)
        }
    }

    private fun extractAlpineRootfs() {
        if (!alpineDir.exists() || alpineDir.listFiles()?.isEmpty() != false) {
            if (!alpineDir.exists()) {
                alpineDir.mkdirs()
            }
            try {
                context.assets.open(ALPINE_ROOTFS_NAME).use { input ->
                    TarExtractor.extract(input, alpineDir)
                }
            } catch (e: Exception) {
                onError("Failed to extract Alpine rootfs: ${e.message}. stack: ${e.stackTraceToString()}")
                alpineDir.delete()
            }
        }
    }

    private fun ensureProotTmpDir() {
        if (!prootTmpDir.exists()) {
            prootTmpDir.mkdirs()
        }
    }

    fun stopShell() {
        try {
            process?.destroy()
            stdoutReader?.close()
            stderrReader?.close()
            stdinWriter?.close()
            stdoutThread?.interrupt()
            stderrThread?.interrupt()
        } catch (e: Exception) {
        } finally {
            process = null
            stdoutReader = null
            stderrReader = null
            stdinWriter = null
        }
    }

    fun sendCommand(command: String) {
        try {
            val fullCommand = "$command; echo ${SENTINEL_PREFIX}_FINISHED: \$?"
            stdinWriter?.write(fullCommand)
            stdinWriter?.newLine()
            stdinWriter?.flush()
        } catch (e: Exception) {
            onError("Failed to send command: ${e.message}")
        }
    }

    fun isRunning(): Boolean = process?.isAlive == true

    companion object {
        private const val SENTINEL_PREFIX = "8f4a6b2c"
        private const val PROOT_NAME = "proot"
        private const val ALPINE_ROOTFS_NAME = "alpinetargz"
        private const val ALPINE_DIR = "alpine"
        private const val PROOT_TMP_DIR = "proot_tmp"
    }
}

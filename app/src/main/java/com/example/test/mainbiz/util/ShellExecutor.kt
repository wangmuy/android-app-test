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
    private val bindMounts: List<String> = emptyList(),
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

    private val extraMountsDir: File
        get() = File(context.filesDir, EXTRA_MOUNTS_DIR)

    fun getBootClassPath(): String {
        val pb = ProcessBuilder("sh", "-c", "echo \$BOOTCLASSPATH")

        var process: Process? = null
        try {
            process = pb.start()
            BufferedReader(InputStreamReader(process.getInputStream(), "UTF-8")).use { reader ->
                val line = reader.readLine()
                val exitCode = process.waitFor()
                onError("BOOTCLASSPATH=$line")
                if (exitCode == 0 && line != null && !line.isEmpty()) {
                    return line
                }
            }
        } catch (e: Exception) {
            onError("error: ${e.stackTraceToString()}")
        } finally {
            process?.destroy()
        }
        return ""
    }


    fun startShell(): Boolean {
        return try {
            onError("Starting shell with bind mounts: $bindMounts")

            extractProot()
            extractTarAndModifyPermission(ALPINE_ROOTFS_NAME, alpineDir,
                mapOf(
                    File(alpineDir, "bin") to "rwx------",
                    File(alpineDir, "sbin") to "rwx------",
                    File(alpineDir, "lib") to "rwx------",
                ))
            ensureProotTmpDir()
            extractTarAndModifyPermission(EXTRA_MOUNTS_NAME, extraMountsDir)

            val alpinePath = alpineDir.absolutePath
            val prootTmpPath = prootTmpDir.absolutePath

            onError("Alpine path: $alpinePath")
            onError("Proot tmp path: $prootTmpPath")

            val processBuilder = ProcessBuilder()
                .redirectErrorStream(false)

            val env = processBuilder.environment()
            env["PATH"] = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/system/bin"
            env["HOME"] = "/root"
            env["PROOT_TMP_DIR"] = prootTmpPath
//            env["PROOT_NO_SECCOMP"] = "1"
            env["BOOTCLASSPATH"] = getBootClassPath()
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
            val commandList = mutableListOf(
                prootFile.absolutePath,
                "--link2symlink",
                "-r", alpinePath,
                "-0",
                "-w", "/root",
                "-b", "/dev",
                "-b", "/proc",
                "-b", "/sys",
                "-b", "/system",
                "-b", "/apex",
            )
            bindMounts.forEach { mount ->
                commandList.add("-b")
                commandList.add(mount)
            }
            EXTRA_MOUNTS.forEach { mount ->
                if (File(extraMountsDir, mount.split(":")[0]).exists()) {
                    commandList.add("-b")
                    commandList.add("${extraMountsDir.absolutePath}/$mount")
                }
            }
            commandList.add("/bin/busybox")
            commandList.add("sh")

            onError("Proot command: ${commandList.joinToString(" ")}")

            processBuilder.command(commandList)
            processBuilder.directory(context.filesDir)

            process = processBuilder.start()

            onError("Shell process started successfully")

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
                    onError("stdout thread error: ${e.message}")
                }
            }

            stderrThread = Thread {
                try {
                    var line: String?
                    while (stderrReader?.readLine().also { line = it } != null) {
                        line?.let { onError(it) }
                    }
                } catch (e: Exception) {
                    onError("stderr thread error: ${e.message}")
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
                    onError("Failed to read stdout: ${e.message}")
                }
            }

            stderrThread = Thread {
                try {
                    var line: String?
                    while (stderrReader?.readLine().also { line = it } != null) {
                        line?.let { onError(it) }
                    }
                } catch (e: Exception) {
                    onError("Failed to read stderr: ${e.message}")
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

    private fun extractTarAndModifyPermission(tarName: String, destDir: File, filePermissions: Map<File, String> = emptyMap()) {
        if (!destDir.exists() || destDir.listFiles()?.isEmpty() != false) {
            if (!destDir.exists()) {
                destDir.mkdirs()
            }
            try {
                context.assets.open(tarName).use { input ->
                    TarExtractor.extract(input, destDir)
                    onError("extract ok: $tarName")
                    filePermissions.forEach { entry ->
                        FilePermissionUtil.setPermissions(entry.key, entry.value)
                    }
                }
            } catch (e: Exception) {
                onError("Failed to extractAndModifyPermission: $tarName, ${e.message}. stack: ${e.stackTraceToString()}")
                destDir.delete()
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
        private const val EXTRA_MOUNTS_NAME = "extramountstargz"
        private const val EXTRA_MOUNTS_DIR = "extramounts"
        private val EXTRA_MOUNTS = arrayOf("linkerconfig:/linkerconfig", "resolv.conf:/etc/resolv.conf")
    }
}

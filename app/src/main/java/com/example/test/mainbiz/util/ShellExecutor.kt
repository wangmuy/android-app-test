package com.example.test.mainbiz.util

import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ShellExecutor(
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

    fun startShell(): Boolean {
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
            false
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
    }
}

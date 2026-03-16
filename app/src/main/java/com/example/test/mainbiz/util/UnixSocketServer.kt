package com.example.test.mainbiz.util

import android.content.Context
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.net.LocalSocketAddress
import java.io.File
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter

class UnixSocketServer(
    private val context: Context,
    private val callback: ShellCallback,
    private val logger: Logger? = null
) {
    companion object {
        private const val TAG = "UnixSocketServer"
    }

    private var serverSocket: LocalServerSocket? = null
    private var isRunning = false
    private var keptSocket: LocalSocket? = null

    val socketPath: String
        get() = File(context.filesDir, "unix_socket_bridge.sock").absolutePath

    fun startServer() {
        if (isRunning) return

        ScriptExtractor.extractScript(context)

        val socketFile = File(socketPath)
        if (socketFile.exists()) {
            socketFile.delete()
        }

        try {
            val address = LocalSocketAddress(socketPath, LocalSocketAddress.Namespace.FILESYSTEM)
            val localSocket = LocalSocket()
            localSocket.bind(address)
            // CRITICAL: We must keep 'localSocket' alive so the FD isn't closed!
            // Storing it in a class member prevents EBADF.
            keptSocket = localSocket
            serverSocket = LocalServerSocket(localSocket.fileDescriptor)
            isRunning = true

            Thread {
                while (isRunning) {
                    try {
                        val clientSocket = serverSocket?.accept()
                        logger?.log(TAG, "client socket accepted")
                        clientSocket?.let { handleClient(it) }
                    } catch (e: Exception) {
                        logger?.log(TAG, "Accept error: ${e.message}")
                    }
                }
            }.start()
            logger?.log(TAG, "unix socket server start ok")
        } catch (e: Exception) {
            logger?.log(TAG, "Start error: ${e.message}")
        }
    }

    private fun handleClient(clientSocket: LocalSocket) {
        try {
            val reader = BufferedReader(InputStreamReader(clientSocket.inputStream))
            val writer = BufferedWriter(OutputStreamWriter(clientSocket.outputStream))

            val request = reader.readLine()

            if (request != null) {
                val (returnCode, response) = callback.call(request)

                writer.write("$returnCode\n")
                writer.write(response)
                writer.newLine()
                writer.flush()
            }

            reader.close()
            writer.close()
            clientSocket.close()
        } catch (e: Exception) {
            logger?.log(TAG, "Handle client error: ${e.message}")
        }
    }

    fun stopServer() {
        isRunning = false
        keptSocket = null
        try {
            serverSocket?.close()
            val socketFile = File(socketPath)
            if (socketFile.exists()) {
                socketFile.delete()
            }
        } catch (e: Exception) {
            logger?.log(TAG, "Stop error: ${e.message}")
        }
    }

    fun isRunning(): Boolean = isRunning
}

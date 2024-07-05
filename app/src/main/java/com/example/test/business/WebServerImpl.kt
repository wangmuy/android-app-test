package com.example.test.business

import android.util.Log
import fi.iki.elonen.SimpleWebServer
import java.io.File

object WebServerImpl {
    private const val TAG = "WebServerImpl"
    private val LOCK = Any()

    private var serverStarted = false
    private var serverThread: Thread? = null
    private var server: SimpleWebServer? = null

    fun startServer(serverBaseDir: File) {
        synchronized(LOCK) {
            if (!serverStarted) {
                serverThread = Thread {
                    synchronized(LOCK) {
                        try {
                            Log.d(TAG, "start beg")
                            server = SimpleWebServer("localhost", 8000, serverBaseDir, false)
                            server?.start()
                            Log.d(TAG, "start ok")
                        } catch (e: InterruptedException) {
                            Log.d(TAG, "interrupted")
                            serverStarted = false
                            serverThread = null
                            server = null
                        }
                    }
                }
                serverThread?.start()
                serverStarted = true
            }
        }
    }

    fun stopServer() {
        synchronized(LOCK) {
            Log.d(TAG, "stop beg")
            server?.stop()
            serverThread?.interrupt()
            serverThread = null
            serverStarted = false
            Log.d(TAG, "stop end")
        }
    }
}
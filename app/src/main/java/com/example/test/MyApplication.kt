package com.example.test

import android.app.Application
import android.util.Log
import fi.iki.elonen.SimpleWebServer
import java.io.File

class MyApplication: Application() {
    companion object {
        private const val TAG = "TestMyApplication"
        private val LOCK = Any()
    }

    private var serverStarted = false
    private var serverThread: Thread? = null
    private var server: SimpleWebServer? = null

    override fun onCreate() {
        super.onCreate()
    }

    // TODO move to service in case of "Could not send response to the client"
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
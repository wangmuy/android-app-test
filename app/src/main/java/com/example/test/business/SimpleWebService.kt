package com.example.test.business

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.test.R
import java.io.File

class SimpleWebService: Service() {
    companion object {
        private const val TAG = "SimpleWebService"
        private const val FOREGROUND_ID = 1
        private const val CHANNEL_ID = "Normal"
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        val channel = NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle("MyWebService")
            .setSmallIcon(R.drawable.android_10_logo)
            .setTicker("simple web service")
            .build()
        startForeground(FOREGROUND_ID, notification)

        try {
            val baseDir = File(filesDir, "testPWA")
            if (!baseDir.exists()) {
                Log.d(TAG, "path not exist: ${baseDir.absolutePath}")
                stopSelf()
            } else {
                WebServerImpl.startServer(baseDir)
                Log.d(TAG, "start done")
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            WebServerImpl.stopServer()
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
package com.example.test.biz1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.test.MyConst

class MyForegroundService : Service() {
    companion object {
        const val TAG = "MyForegroundService${MyConst.TAG}"
        const val CHANNEL_ID = "MyForegroundServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        val channel = NotificationChannel(CHANNEL_ID, "MyFGSVC", NotificationManager.IMPORTANCE_DEFAULT)
        val notiMgr = getSystemService(NotificationManager::class.java)
        notiMgr.createNotificationChannel(channel)

        // Create a notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MyForegroundService")
            .setContentText("I am running")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .build()

        // Start foreground service
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(MyConst.TAG, "onStartCommand intent=$intent, flags=$flags, startId=$startId")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(MyConst.TAG, "onBind intent=$intent")
        return null
    }

    override fun onDestroy() {
        Log.d(MyConst.TAG, "onDestroy")
        super.onDestroy()
    }
}
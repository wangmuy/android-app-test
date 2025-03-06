package com.example.test.biz1

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.test.MyConst

class MyBackgroundService : Service() {
    companion object {
        const val TAG = "MyBackgroundService${MyConst.TAG}"
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand intent=$intent, flags=$flags, startId=$startId")
        doBackgroundWork()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind, intent=$intent")
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    private fun doBackgroundWork() {
        Thread {
            try {
                Thread.sleep(5000) // Simulate a task
                Log.d(TAG, "Background task completed")
            } catch (e: InterruptedException) {
                Log.e(TAG, "", e)
            }
            // Consider stopping the service once the task is done
            stopSelf()
        }.start()
    }
}
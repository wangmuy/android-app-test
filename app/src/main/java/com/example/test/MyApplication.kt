package com.example.test

import android.app.Application
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MyApplication: Application() {
    companion object {
        private const val TAG = "${Const.PREFIX}MyApplication"
    }

    override fun onCreate() {
        super.onCreate()

        try {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }
}
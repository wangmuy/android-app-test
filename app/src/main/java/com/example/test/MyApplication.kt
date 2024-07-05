package com.example.test

import android.app.Application

class MyApplication: Application() {
    companion object {
        private const val TAG = "TestMyApplication"
    }

    override fun onCreate() {
        super.onCreate()
    }
}
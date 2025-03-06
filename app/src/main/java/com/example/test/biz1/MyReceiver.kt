package com.example.test.biz1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.test.MyConst

class MyReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "MyReceiver${MyConst.TAG}"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive intent=$intent")
    }
}
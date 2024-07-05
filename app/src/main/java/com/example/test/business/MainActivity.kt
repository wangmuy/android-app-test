package com.example.test.business

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "TestMainActivity"
    }

    private val serviceIntent by lazy { Intent(this, SimpleWebService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnStart).setOnClickListener{v->
            try {
                val started = startService(serviceIntent)
                Log.d(TAG, "startService $started")
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            }
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener{v->
            val stopped = stopService(serviceIntent)
            Log.d(TAG, "stopService $stopped")
        }
    }
}

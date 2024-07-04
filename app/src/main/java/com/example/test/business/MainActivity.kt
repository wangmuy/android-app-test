package com.example.test.business

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.test.MyApplication
import com.example.test.R
import java.io.File

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "TestMainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnStart).setOnClickListener{v->
            try {
                val baseDir = File(filesDir, "testPWA")
                if (!baseDir.exists()) {
                    Log.d(TAG, "path not exist: ${baseDir.absolutePath}")
                } else {
                    (application as MyApplication).startServer(baseDir)
                    Log.d(TAG, "start done")
                }
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            }
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener{v->
            try {
                (application as MyApplication).stopServer()
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            }
        }
    }
}

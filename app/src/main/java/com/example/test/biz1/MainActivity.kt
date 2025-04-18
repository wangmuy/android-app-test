package com.example.test.biz1

import android.app.appfunctions.AppFunctionManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R

class MainActivity: AppCompatActivity() {
    companion object {
        const val TAG = "MainActivityTAG"
    }
    private lateinit var mLogView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLogView = findViewById(R.id.logTv)

        findViewById<Button>(R.id.envBtn).setOnClickListener {
            checkEnv()
        }
    }

    private fun checkEnv() {
        try {
            val mgr = getSystemService(Context.APP_FUNCTION_SERVICE) as AppFunctionManager
            setLog("mgr=$mgr")
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}", e)
            mLogView.text = "${e.message}"
        }
    }

    private fun setLog(text: String?) {
        text?.let {
            mLogView.post { mLogView.text = it }
        }
    }

    private fun addLog(text: String?) {
        text?.let {
            mLogView.post { mLogView.text = it + mLogView.text.toString() }
        }
    }
}

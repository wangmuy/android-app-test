package com.example.test.biz1

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.test.MyConst
import com.example.test.R

class TestActivity : AppCompatActivity() {
    companion object {
        const val TAG = "TestActivity${MyConst.TAG}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        findViewById<TextView>(R.id.test_log_tv).setText("I am $this")
    }
}
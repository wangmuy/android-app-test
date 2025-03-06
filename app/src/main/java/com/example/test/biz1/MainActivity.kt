package com.example.test.biz1

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.test.MyConst
import com.example.test.R

class MainActivity: AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity${MyConst.TAG}"
    }

    lateinit var logView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logView = findViewById(R.id.log_tv)

        findViewById<Button>(R.id.pi_act).setOnClickListener {
            startActivityByPendingIntent()
        }

        findViewById<Button>(R.id.pi_fg_svc).setOnClickListener {
            startForegroundServiceByPendingIntent()
        }

        findViewById<Button>(R.id.pi_bg_svc).setOnClickListener {
            startBackgroundServiceByPendingIntent()
        }

        findViewById<Button>(R.id.pi_bc).setOnClickListener {
            startBroadcastByPendingIntent()
        }
    }

    private fun startActivityByPendingIntent() {
        val intent = Intent(this, TestActivity::class.java)
        val pi = PendingIntent.getActivity(this, 1, intent, 0)
        pi.send()
    }

    private fun startForegroundServiceByPendingIntent() {
        val intent = Intent(this, MyForegroundService::class.java)
        val pi = PendingIntent.getForegroundService(this, 2, intent, 0)
        pi.send()
    }

    private fun startBackgroundServiceByPendingIntent() {
        val intent = Intent(this, MyBackgroundService::class.java)
        val pi = PendingIntent.getService(this, 3, intent, 0)
        pi.send()
    }

    private fun startBroadcastByPendingIntent() {
        val intent = Intent(this, MyReceiver::class.java)
        val pi = PendingIntent.getBroadcast(this, 4, intent, 0)
        pi.send()
    }
}

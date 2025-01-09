package com.example.test.biz1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sdk.ISkillCallback
import com.example.sdk.ISkillRequest
import com.example.sdk.ISkillService
import com.example.test.R
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivityTAG"
    }
    private lateinit var logView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "main tid=${Thread.currentThread().id}")
        logView = findViewById(R.id.log)
        findViewById<Button>(R.id.clearlog).setOnClickListener {
            logView.setText("")
        }
        findViewById<Button>(R.id.func1).setOnClickListener {
            Thread {
                callFunc("$packageName/${MyFuncCall.CALL_1}", """
{
  "serviceId": "$packageName",
  "operationId": "${MyFuncCall.CALL_1}",
  "params": {
    "param1": "hello"
  }
}
                """.trimIndent())
            }.start()
        }
        findViewById<Button>(R.id.func2).setOnClickListener {
            Thread {
                callFunc("$packageName/${MyFuncCall.CALL_2}", """
{
  "serviceId": "$packageName",
  "operationId": "${MyFuncCall.CALL_2}",
  "params": {
    "param1": "world",
    "param2": "p2"
  }
}                   
                """.trimIndent())
            }.start()
        }
    }

    fun log(tag: String, msg: String) = logView.post {
        Log.d(tag, msg)
        logView.setText("$msg\n${logView.text}")
    }

    fun callFunc(funcId: String, cmd: String) {
        log(TAG, "\n\n")
        Log.d(TAG, "callFunc tid=${Thread.currentThread().id}")
        val intent = Intent().apply {
            setComponent(ComponentName(packageName, "com.example.funccallspec.FuncCallService"))
        }
        var skillSvc: ISkillService? = null
        val conn = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(TAG, "onServiceConnected tid=${Thread.currentThread().id}")
                skillSvc = ISkillService.Stub.asInterface(service)
                skillSvc?.call(funcId, cmd, null, object: ISkillCallback.Stub() {
                    override fun onProgress(request: ISkillRequest?, progress: Int, max: Int) {
                        Log.d(TAG, "onProgress tid=${Thread.currentThread().id}")
                        log(TAG, "onProgress: progress=$progress, max=$max")
                    }

                    override fun onStatusChanged(request: ISkillRequest?, statusCode: Int) {
                        Log.d(TAG, "onStatusChanged tid=${Thread.currentThread().id}")
                        log(TAG, "onStatusChanged: statusCode=$statusCode")
                    }

                    override fun notify(request: ISkillRequest?, resultCode: Int, bundle: Bundle?) {
                        Log.d(TAG, "notify tid=${Thread.currentThread().id}")
                        log(TAG, "notify: resultCode=$resultCode, bundle=$bundle")
                    }
                })
                unbindService(this)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(TAG, "onServiceDisconnected tid=${Thread.currentThread().id}")
            }
        }
        val bindOk = bindService(intent, conn, Context.BIND_AUTO_CREATE)
        Log.d(TAG, "bindOk=$bindOk")
    }
}

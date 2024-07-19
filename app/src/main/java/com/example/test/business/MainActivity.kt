package com.example.test.business

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.example.test.Const
import com.example.test.R

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "${Const.PREFIX}MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.exePyBtn).setOnClickListener{v->
            onPyExe()
        }
    }

    private fun onPyExe() {
        Thread {
            try {
                val py = Python.getInstance()
                py.getModule("main").callAttr("main")
                Log.d(TAG, "pyExe end")
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            }
        }.start()
    }
}

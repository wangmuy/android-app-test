package com.example.test.business

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.example.test.Const
import com.example.test.R

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "${Const.PREFIX}MainActivity"
    }

    lateinit var pyCodeEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.exePyBtn).setOnClickListener{v->
            onPyExe()
        }

        findViewById<Button>(R.id.exePyDynBtn).setOnClickListener{v->
            onPyDynExe()
        }

        pyCodeEdit = findViewById(R.id.pyCodeEdit)
    }

    // python.stdout: helloPython from pyExe
    private fun onPyExe() {
        Thread {
            try {
                val py = Python.getInstance()
                py.getModule("main").callAttr("main")
                Log.d(TAG, "pyExe end")
            } catch (e: Exception) {
                Log.e(TAG, "pyExe", e)
            }
        }.start()
    }

    // python.stdout: helloPython from pyDynExe
    private fun onPyDynExe() {
        val pyCode = pyCodeEdit.text.toString()
        Log.d(TAG, "onPyDynExe pyCode=$pyCode")
        Thread {
            try {
                val py = Python.getInstance()
                val ret = py.builtins.callAttr("eval", pyCode, py.builtins.callAttr("dict"))
                Log.d(TAG, "pyDynExe end, ret=$ret")
            } catch (e: Exception) {
                Log.e(TAG, "pyDynExe", e)
            }
        }.start()
    }
}

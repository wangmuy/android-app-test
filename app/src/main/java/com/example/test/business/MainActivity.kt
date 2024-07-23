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

        private const val EXAMPLE_NUMPY = """
import numpy as np
arr = np.array([1, 2, 3, 4, 5, 6, 7, 8])
reversed_arr = np.flip(arr)
print('Reversed Array: ', reversed_arr)
"""

        private const val EXAMPLE_SCIPY = """
import numpy as np
from scipy import linalg
A = np.array([[1,2], [4,3]])
B = linalg.inv(A)
print(B)
"""

        private const val EXAMPLE_PANDAS = """
import pandas as pd
data = {
    "product": ["A", "B", "C", "C", "D"],
    "price": [22000, 27000, 25000, 29000, 35000],
    "year": [2014, 2015, 2016, 2017, 2018],
}
df = pd.DataFrame(data)
stats_numeric = df["price"].describe()
print(stats_numeric)
"""
    }

    lateinit var pyCodeEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.exePyBtn).setOnClickListener{v->
            onPyExe("main")
        }

        findViewById<Button>(R.id.numpyModuleBtn).setOnClickListener{v->
            onPyExe("testnumpy")
        }

        findViewById<Button>(R.id.scipyModuleBtn).setOnClickListener{v->
            onPyExe("testscipy")
        }

        findViewById<Button>(R.id.pandasModuleBtn).setOnClickListener{v->
            onPyExe("testpandas")
        }

        findViewById<Button>(R.id.exePyDynBtn).setOnClickListener{v->
            onPyDynExe()
        }

        pyCodeEdit = findViewById(R.id.pyCodeEdit)

        findViewById<Button>(R.id.numpyExampleBtn).setOnClickListener{v->
            pyCodeEdit.setText(EXAMPLE_NUMPY)
        }
        findViewById<Button>(R.id.scipyExampleBtn).setOnClickListener{v->
            pyCodeEdit.setText(EXAMPLE_SCIPY)
        }
        findViewById<Button>(R.id.pandasExampleBtn).setOnClickListener{v->
            pyCodeEdit.setText(EXAMPLE_PANDAS)
        }
    }

    // python.stdout: helloPython from pyExe
    private fun onPyExe(moduleName: String) {
        Thread {
            try {
                val py = Python.getInstance()
                py.getModule(moduleName).callAttr("main")
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
                val globals = py.builtins.callAttr("dict")
                val ret = py.builtins.callAttr("exec", pyCode, globals)
                Log.d(TAG, "pyDynExe end, ret=$ret")
            } catch (e: Exception) {
                Log.e(TAG, "pyDynExe", e)
            }
        }.start()
    }
}

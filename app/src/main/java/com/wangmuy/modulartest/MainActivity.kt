package com.wangmuy.modulartest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wangmuy.modulartest.Const.DEBUG_TAG
import com.wangmuy.modulartest.feat2.IFeat2
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity$DEBUG_TAG"
    }

    private val feat2: IFeat2 by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(appModule)
        setContent {
            Greeting("Android")
        }
    }

    override fun onResume() {
        super.onResume()
        val result = feat2.doFeat2("yo")
        Log.d(TAG, "result=$result")
        // val feat1Impl: Feat1Impl? = null // error
        // val feat2Impl: Feat2Impl? = null // error
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(appModule)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting("Android")
}
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
import com.wangmuy.modulartest.feat3.Feat3Biz
import com.wangmuy.modulartest.feat3.IFeat3
import com.wangmuy.modulartest.feat3.loadFeat32Module
import com.wangmuy.modulartest.feat3.unloadFeat32Module
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity$DEBUG_TAG"
    }

    private val feat2: IFeat2 by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        loadKoinModules(appModule)
        setContent {
            Greeting("Android")
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val feat2Result = feat2.doFeat2("yo")
            Log.d(TAG, "feat2Result=$feat2Result")
            // val feat1Impl: Feat1Impl? = null // error
            // val feat2Impl: Feat2Impl? = null // error

            // https://stackoverflow.com/a/60508841
            val feat3Biz = Feat3Biz("key", "yo")
            var feat3List: List<IFeat3> = getKoin().getAll()
            Log.d(TAG, "initial feat3 List.size=${feat3List.size}")
            doFeat3List(feat3Biz, feat3List)

            loadFeat32Module()
            feat3List = getKoin().getAll()
            Log.d(TAG, "load feat32 List.size=${feat3List.size}")
            doFeat3List(feat3Biz, feat3List)

            unloadFeat32Module()
            feat3List = getKoin().getAll()
            Log.d(TAG, "unload feat32 List.size=${feat3List.size}")
            doFeat3List(feat3Biz, feat3List)
        } catch (t: Throwable) {
            Log.e(TAG, "", t)
        }
    }

    private fun doFeat3List(feat3Biz: Feat3Biz, feat3List: List<IFeat3>) {
        feat3List.forEach {feat3->
            val feat3Result = feat3.doFeat3(feat3Biz)
            Log.d(TAG, "feat3=$feat3, feat3Result=$feat3Result")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        unloadKoinModules(appModule)
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
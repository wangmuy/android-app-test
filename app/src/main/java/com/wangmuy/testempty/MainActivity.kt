package com.wangmuy.testempty

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wangmuy.testempty.Const.DEBUG_TAG
import com.wangmuy.testempty.di.DaggerMainActivityComponent
import com.wangmuy.testempty.feat1.IFeat1
import com.wangmuy.testempty.feat2.IFeat2
import com.wangmuy.testempty.feat3.Feat3Biz
import com.wangmuy.testempty.feat3.IFeat3
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity$DEBUG_TAG"
    }

    @Inject
    lateinit var remoteRepo: RemoteRepo

    @Inject
    lateinit var feat1: IFeat1

    @Inject
    lateinit var cachedItems: Set<@JvmSuppressWildcards Item>

    @Inject
    lateinit var feat2: IFeat2

    @Inject
    lateinit var feat3List: Map<String, @JvmSuppressWildcards IFeat3>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDaggerInject()
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        try {
            Log.d(TAG, "remoteRepo=$remoteRepo")
            Log.d(TAG, "cachedItems=$cachedItems")

            val feat1Result = feat1.doFeat1(10)
            Log.d(TAG, "feat1=$feat1, feat1Result=$feat1Result")
//            val feat1Impl = Feat1Impl(this, RemoteRepo()) // error

            val feat2Result = feat2.doFeat2("yo")
            Log.d(TAG, "feat2Result=$feat2Result")

            val feat3Biz = Feat3Biz("key", "yo")
            Log.d(TAG, "feat3List=$feat3List")
            doFeat3List(feat3Biz, feat3List)
        } catch (t: Throwable) {
            Log.e(TAG, "", t)
        }
    }

    private fun doFeat3List(feat3Biz: Feat3Biz, feat3List: Map<String, IFeat3>) {
        feat3List.forEach {entry->
            val key = entry.key
            val feat3 = entry.value
            val feat3Result = feat3.doFeat3(feat3Biz)
            Log.d(TAG, "key=$key, feat3=$feat3, feat3Result=$feat3Result")
        }
    }

    private fun initDaggerInject() {
        DaggerMainActivityComponent.builder()
            .baseComponent(baseComponent())
            .build()
            .inject(this)
    }
}
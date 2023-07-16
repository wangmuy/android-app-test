package com.wangmuy.testempty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wangmuy.testempty.Const.DEBUG_TAG
import com.wangmuy.testempty.di.DaggerMainActivityComponent
import com.wangmuy.testempty.feat1.IFeat1
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
            Log.d(TAG, "feat1Result=$feat1Result")
//            val feat1Impl = Feat1Impl(this, RemoteRepo()) // error
        } catch (t: Throwable) {
            Log.e(TAG, "", t)
        }
    }

    private fun initDaggerInject() {
        DaggerMainActivityComponent.builder()
            .baseComponent(baseComponent())
            .build()
            .inject(this)
    }
}
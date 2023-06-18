package com.wangmuy.modulartest.feat1

import android.content.Context
import android.util.Log
import com.wangmuy.modulartest.Const.DEBUG_TAG

class Feat1Impl(private val context: Context): IFeat1 {
    companion object {
        private const val TAG = "Feat1Impl$DEBUG_TAG"
    }

    override fun doFeat1(x: Int): Int {
        Log.d(TAG, "appName=${context.applicationInfo.name}")
        return x*x
    }
}
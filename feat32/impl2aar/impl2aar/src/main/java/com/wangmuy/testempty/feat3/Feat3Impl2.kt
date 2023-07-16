package com.wangmuy.testempty.feat3

import android.util.Log

class Feat3Impl2: IFeat3 {
    companion object {
        private const val TAG = "Feat3Impl2Dagger-wmywmy"
    }
    override fun doFeat3(biz: Feat3Biz): Feat3Biz {
        Log.d(TAG, "doFeat32")
        return biz.apply {
            value += (value + value)
        }
    }
}
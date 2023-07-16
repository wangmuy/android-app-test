package com.wangmuy.testempty.feat3

import android.util.Log

class Feat3Impl: IFeat3 {
    companion object {
        private const val TAG = "Feat3ImplDagger-wmywmy"
    }
    override fun doFeat3(biz: Feat3Biz): Feat3Biz {
        Log.d(TAG, "doFeat3")
        return biz.apply {
            value += value
        }
    }
}
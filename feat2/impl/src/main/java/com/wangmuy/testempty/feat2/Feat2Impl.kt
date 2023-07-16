package com.wangmuy.testempty.feat2

import android.util.Log
import com.wangmuy.testempty.Const.DEBUG_TAG
import com.wangmuy.testempty.feat1.IFeat1

class Feat2Impl(private val feat1: IFeat1): IFeat2 {
    companion object {
        private const val TAG = "Feat2Impl$DEBUG_TAG"
    }

    // private val feat1Impl: Feat1Impl // error
    override fun doFeat2(s: String): String {
        Log.d(TAG, "feat1=$feat1")
        return s + s + feat1.doFeat1(42)
    }
}
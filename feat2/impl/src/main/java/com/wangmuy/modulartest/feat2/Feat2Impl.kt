package com.wangmuy.modulartest.feat2

import com.wangmuy.modulartest.feat1.IFeat1

class Feat2Impl(private val feat1: IFeat1): IFeat2 {
    // private val feat1Impl: Feat1Impl // error
    override fun doFeat2(s: String): String {
        return s + s + feat1.doFeat1(42)
    }
}
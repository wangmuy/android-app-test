package com.wangmuy.modulartest.feat3

class Feat3Impl: IFeat3 {
    override fun doFeat3(biz: Feat3Biz): Feat3Biz {
        return biz.apply {
            value += value
        }
    }
}
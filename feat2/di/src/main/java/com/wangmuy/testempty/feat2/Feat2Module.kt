package com.wangmuy.testempty.feat2

import com.wangmuy.testempty.feat1.IFeat1
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class Feat2Module {

    @Reusable
    @Provides
    fun provideFeat2(feat1: IFeat1): IFeat2 = Feat2Impl(feat1)
}
package com.wangmuy.testempty.feat3

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
class Feat32Module {

    @Provides
    @IntoMap
    @StringKey("feat32")
    fun provideFeat32(): IFeat3 = Feat3Impl2()
}
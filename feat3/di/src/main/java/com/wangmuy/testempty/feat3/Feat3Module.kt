package com.wangmuy.testempty.feat3

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
class Feat3Module {

    @Provides
    @IntoMap
    @StringKey("feat31")
    fun provideFeat3(): IFeat3 = Feat3Impl()
}
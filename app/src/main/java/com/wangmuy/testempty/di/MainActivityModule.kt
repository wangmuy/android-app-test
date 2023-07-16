package com.wangmuy.testempty.di

import com.wangmuy.testempty.Item
import com.wangmuy.testempty.feat3.IFeat3
import dagger.Module
import dagger.multibindings.Multibinds

@Module
abstract class MainActivityModule {
    @Multibinds abstract fun cachedItems(): Set<Item>

    @Multibinds abstract fun feat3List(): Map<String, IFeat3>
}
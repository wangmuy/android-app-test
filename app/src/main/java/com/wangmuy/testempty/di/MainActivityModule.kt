package com.wangmuy.testempty.di

import com.wangmuy.testempty.Item
import dagger.Module
import dagger.multibindings.Multibinds

@Module
abstract class MainActivityModule {
    @Multibinds abstract fun cachedItems(): Set<Item>
}
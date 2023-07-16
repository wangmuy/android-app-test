package com.wangmuy.testempty.di

import com.wangmuy.testempty.Item
import com.wangmuy.testempty.RemoteRepo
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import kotlinx.coroutines.runBlocking

@Module
class BaseItemsModule {
    @Provides
    @ElementsIntoSet
    fun provideCachedItems(remoteRepo: RemoteRepo): Set<Item> = runBlocking { remoteRepo.getAll().toSet() }
}
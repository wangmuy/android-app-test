package com.wangmuy.testempty.di

import com.wangmuy.testempty.DispatchersProvider
import com.wangmuy.testempty.Item
import com.wangmuy.testempty.RemoteRepo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.Multibinds
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
class BaseModule {

    @Singleton
    @Provides
    fun provideRemoteRepo(): RemoteRepo = RemoteRepo("remoteRepo")

    @Reusable
    @Provides
    fun provideDispatchersProvider() = DispatchersProvider()
}
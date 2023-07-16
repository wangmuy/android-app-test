package com.wangmuy.testempty.di

import android.content.Context
import com.wangmuy.testempty.DispatchersProvider
import com.wangmuy.testempty.RemoteRepo
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ BaseModule::class, ContextModule::class ])
interface BaseComponent {
    fun context(): Context
    fun remoteRepo(): RemoteRepo
    fun provideDispatchersProvider(): DispatchersProvider
}
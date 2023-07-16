package com.wangmuy.testempty

import android.app.Application
import com.wangmuy.testempty.Const.DEBUG_TAG
import com.wangmuy.testempty.di.BaseComponent
import com.wangmuy.testempty.di.BaseComponentProvider
import com.wangmuy.testempty.di.ContextModule
import com.wangmuy.testempty.di.DaggerBaseComponent

class MainApplication: Application(), BaseComponentProvider {
    companion object {
        private const val TAG = "MainApplication$DEBUG_TAG"
    }

    lateinit var baseComponent: BaseComponent

    override fun onCreate() {
        super.onCreate()
        baseComponent = DaggerBaseComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }

    override fun provideBaseComponent(): BaseComponent = baseComponent
}
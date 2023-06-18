package com.wangmuy.modulartest

import android.app.Application
import com.wangmuy.modulartest.Const.DEBUG_TAG
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication: Application() {
    companion object {
        private const val TAG = "MainApplication$DEBUG_TAG"
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
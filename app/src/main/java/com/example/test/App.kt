package com.example.test

import android.app.Application
import com.example.test.di.databaseModule
import com.example.test.di.networkModule
import com.example.test.di.repositoryModule
import com.example.test.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            androidLogger()

            modules(
//                databaseModule,
//                networkModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}
package com.example.test

import com.example.test.util.schedulers.BaseSchedulerProvider
import com.example.test.util.schedulers.SchedulerProvider

object Injection {
    fun providerSchedulerProvider(): BaseSchedulerProvider {
        return SchedulerProvider.getInstance()
    }
}

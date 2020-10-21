package com.example.test

import android.content.Context
import com.example.test.tasks.data.source.TasksRepository
import com.example.test.tasks.data.source.local.TasksLocalDataSource
import com.example.test.tasks.data.source.local.ToDoDatabase
import com.example.test.tasks.data.source.remote.TasksRemoteDataSource
import com.example.test.tasks.domain.usecase.GetTasks
import com.example.test.util.schedulers.BaseSchedulerProvider
import com.example.test.util.schedulers.SchedulerProvider

object Injection {
    fun providerSchedulerProvider(): BaseSchedulerProvider {
        return SchedulerProvider.getInstance()
    }
}

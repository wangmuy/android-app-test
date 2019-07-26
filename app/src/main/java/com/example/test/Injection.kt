package com.example.test

import android.content.Context
import com.example.test.data.source.TasksRepository
import com.example.test.data.source.local.TasksLocalDataSource
import com.example.test.data.source.local.ToDoDatabase
import com.example.test.data.source.remote.TasksRemoteDataSource
import com.example.test.tasks.domain.usecase.GetTasks
import com.example.test.util.AppExecutors
import com.example.test.util.schedulers.BaseSchedulerProvider
import com.example.test.util.schedulers.SchedulerProvider

object Injection {
    private fun provideTasksRepository(context: Context): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(
                TasksRemoteDataSource,
                TasksLocalDataSource.getInstance(AppExecutors(), database.taskDao()))
    }

    fun provideUseCaseGetTasks(context: Context): GetTasks {
        return GetTasks(provideTasksRepository(context))
    }

    fun providerSchedulerProvider(): BaseSchedulerProvider {
        return SchedulerProvider.getInstance()
    }
}

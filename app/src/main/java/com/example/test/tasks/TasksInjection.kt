package com.example.test.tasks

import android.content.Context
import com.example.test.tasks.data.source.TasksRepository
import com.example.test.tasks.data.source.local.TasksLocalDataSource
import com.example.test.tasks.data.source.local.ToDoDatabase
import com.example.test.tasks.data.source.remote.TasksRemoteDataSource
import com.example.test.tasks.domain.usecase.GetTasks

object TasksInjection {
    private fun provideTasksRepository(context: Context): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(
                TasksRemoteDataSource,
                TasksLocalDataSource.getInstance(database.taskDao()))
    }

    fun provideUseCaseGetTasks(context: Context): GetTasks {
        return GetTasks(provideTasksRepository(context))
    }
}
package com.example.test

import android.content.Context
import com.example.test.data.source.TasksRepository
import com.example.test.data.source.local.TasksLocalDataSource
import com.example.test.data.source.local.ToDoDatabase
import com.example.test.data.source.remote.TasksRemoteDataSource
import com.example.test.util.AppExecutors

object Injection {
    fun provideTasksRepository(context: Context): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(
                TasksRemoteDataSource,
                TasksLocalDataSource.getInstance(AppExecutors(), database.taskDao()))
    }
}

package com.wangmuy.mvvmtest.data

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksStream(): Flow<List<Task>>

    suspend fun createTask(title: String, description: String): String
}
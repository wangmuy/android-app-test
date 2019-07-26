package com.example.test.data.source

import com.example.test.data.model.Task
import io.reactivex.Flowable

interface TasksDataSource {
    fun getTask(taskId: String): Flowable<Task?>
    fun getTaskList(): Flowable<List<Task>>
    fun saveTask(task: Task): Int
    fun deleteTask(taskId: String): Int
    fun deleteAllTasks(): Int
    fun refreshTasks()
}

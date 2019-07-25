package com.example.test.data.source

import com.example.test.data.model.Task

interface TasksDataSource {
    interface LoadTasksCallback {
        fun onTasksLoaded(tasks: List<Task>)
        fun onDataNotAvailable()
    }

    interface GetTaskCallback {
        fun onTaskLoaded(task: Task)
        fun onDataNotAvailable()
    }

    fun getTask(taskId: String, callback: GetTaskCallback)
    fun getTaskList(callback: LoadTasksCallback)
    fun saveTask(task: Task)
    fun deleteTask(taskId: String)
    fun deleteAllTasks()
    fun refreshTasks()
}

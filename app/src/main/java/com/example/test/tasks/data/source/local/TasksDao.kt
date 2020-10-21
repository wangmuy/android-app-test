package com.example.test.tasks.data.source.local

import com.example.test.tasks.data.model.Task

interface TasksDao {
    fun getTaskList(): List<Task>
    fun getTask(taskId: String): Task?
    fun saveTask(task: Task): Int
    fun deleteTask(taskId: String): Int
    fun deleteTasks(): Int
}

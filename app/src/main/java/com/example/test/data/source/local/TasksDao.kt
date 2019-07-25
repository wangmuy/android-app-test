package com.example.test.data.source.local

import com.example.test.data.model.Task

interface TasksDao {
    fun getTaskList(): List<Task>
    fun getTask(taskId: String): Task?
    fun saveTask(task: Task): Int
    fun deleteTask(taskId: String): Int
    fun deleteTasks()
}
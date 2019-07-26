package com.example.test.data.source.local

import com.example.test.data.model.Task
import com.example.test.data.source.TasksDataSource
import com.example.test.util.AppExecutors
import io.reactivex.Flowable

class TasksLocalDataSource private constructor(private val tasksDao: TasksDao): TasksDataSource {

    override fun getTaskList(): Flowable<List<Task>> {
        val taskList = tasksDao.getTaskList()
        return Flowable.just(taskList)
    }

    override fun getTask(taskId: String): Flowable<Task?> {
        val task = tasksDao.getTask(taskId)
        return Flowable.just(task)
    }

    override fun saveTask(task: Task): Int {
        return tasksDao.saveTask(task)
    }

    override fun deleteTask(taskId: String): Int {
        return tasksDao.deleteTask(taskId)
    }

    override fun deleteAllTasks(): Int {
        return tasksDao.deleteTasks()
    }

    override fun refreshTasks() {
        // empty
    }

    companion object {
        private var INSTANCE: TasksLocalDataSource? = null

        @JvmStatic
        fun getInstance(tasksDao: TasksDao): TasksLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TasksLocalDataSource::javaClass) {
                    INSTANCE = TasksLocalDataSource(tasksDao)
                }
            }
            return INSTANCE!!
        }
    }
}

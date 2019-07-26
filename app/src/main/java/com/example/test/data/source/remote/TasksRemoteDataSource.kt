package com.example.test.data.source.remote

import com.example.test.data.model.Task
import com.example.test.data.source.TasksDataSource
import io.reactivex.Flowable

object TasksRemoteDataSource: TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L
    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Put Elephant", "3 steps")
        addTask("Guest Magic Number", "42")
    }

    private fun addTask(title:String, description: String) {
        val newTask = Task(title, description)
        TASKS_SERVICE_DATA.put(newTask.id, newTask)
    }

    override fun getTask(taskId: String): Flowable<Task?> {
        val task = TASKS_SERVICE_DATA[taskId]
        Thread.sleep(SERVICE_LATENCY_IN_MILLIS)
        return Flowable.just(task)
    }

    override fun getTaskList(): Flowable<List<Task>> {
        val tasks = ArrayList(TASKS_SERVICE_DATA.values)
        Thread.sleep(SERVICE_LATENCY_IN_MILLIS)
        return Flowable.just(tasks)
    }

    override fun saveTask(task: Task): Int {
        TASKS_SERVICE_DATA.put(task.id, task)
        return 1
    }

    override fun deleteTask(taskId: String): Int {
        TASKS_SERVICE_DATA.remove(taskId)
        return 1
    }

    override fun deleteAllTasks(): Int {
        TASKS_SERVICE_DATA.clear()
        return 1
    }

    override fun refreshTasks() {
        // empty
    }

}
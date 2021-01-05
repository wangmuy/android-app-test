package com.example.test.tasks.data.source.remote

import android.util.Log
import com.example.test.tasks.data.model.Task
import com.example.test.tasks.data.source.TasksDataSource
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

private const val TAG = "TasksRemoteDataSource"

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
        return Flowable.just(task)
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
    }

    override fun getTaskList(): Flowable<List<Task>> {
        return Flowable.fromIterable(TASKS_SERVICE_DATA.values)
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList()
                .toFlowable()
                .doOnNext { Log.d(TAG, "getTaskList tid=" + Thread.currentThread().id) }
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

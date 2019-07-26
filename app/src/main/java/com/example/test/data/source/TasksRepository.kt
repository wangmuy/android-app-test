package com.example.test.data.source

import com.example.test.data.model.Task
import io.reactivex.Flowable

class TasksRepository(
        val tasksRemoteDataSource: TasksDataSource,
        val tasksLocalDataSource: TasksDataSource
): TasksDataSource {
    var cachedTasks: LinkedHashMap<String, Task> = LinkedHashMap()
    var cacheIsDirty = false

    override fun getTask(taskId: String): Flowable<Task?> {
        val taskInCache = getTaskWithId(taskId)

        if (taskInCache != null) {
            return Flowable.just(taskInCache)
        }

        val localTask = tasksLocalDataSource.getTask(taskId).doOnNext{
            if (it != null) {
                cacheAndPerform(it, {})
            }
        }.firstElement().toFlowable()
        val remoteTask = tasksRemoteDataSource.getTask(taskId).doOnNext {
            if (it != null) {
                cacheAndPerform(it, {})
            }
        }
        return Flowable.concat(localTask, remoteTask)
                .firstElement()
                .toFlowable()
    }

    override fun getTaskList(): Flowable<List<Task>> {
        if (cachedTasks.isNotEmpty() && !cacheIsDirty) {
            return Flowable.just(ArrayList(cachedTasks.values))
        }

        cachedTasks.clear()
        tasksLocalDataSource.deleteAllTasks()
        val remoteTasks = tasksRemoteDataSource.getTaskList().doOnNext{
            refreshCache(it)
            refreshLocalDataSource(it)
        }
        if (cacheIsDirty) {
            return remoteTasks
        } else {
            val localTasks = tasksLocalDataSource.getTaskList().doOnNext {
                refreshCache(it)
            }
            return Flowable.concat(localTasks, remoteTasks)
                    .filter{tasks -> tasks.isNotEmpty()}
                    .firstOrError()
                    .toFlowable()
        }
    }

    override fun saveTask(task: Task): Int {
        var localCount = 0
        var remoteCount = 0
        cacheAndPerform(task) {
            remoteCount = tasksRemoteDataSource.saveTask(it)
            localCount = tasksLocalDataSource.saveTask(it)
        }
        return localCount + remoteCount
    }

    override fun deleteTask(taskId: String): Int {
        var localCount = 0
        var remoteCount = 0
        remoteCount = tasksRemoteDataSource.deleteTask(taskId)
        localCount = tasksLocalDataSource.deleteTask(taskId)
        cachedTasks.remove(taskId)
        return localCount + remoteCount
    }

    override fun deleteAllTasks(): Int {
        var localCount = 0
        var remoteCount = 0
        remoteCount = tasksRemoteDataSource.deleteAllTasks()
        localCount = tasksLocalDataSource.deleteAllTasks()
        cachedTasks.clear()
        return localCount + remoteCount
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    private fun getTaskWithId(id: String) = cachedTasks[id]

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        tasks.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalDataSource.deleteAllTasks()
        for (task in tasks) {
            tasksLocalDataSource.saveTask(task)
        }
    }

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        val cachedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = task.isCompleted
        }
        cachedTasks.put(cachedTask.id, cachedTask)
        perform(cachedTask)
    }

    companion object {
        private var INSTANCE: TasksRepository? = null

        @JvmStatic fun getInstance(
                tasksRemoteDataSource: TasksDataSource,
                tasksLocalDataSource: TasksDataSource): TasksRepository {
            return INSTANCE ?: TasksRepository(tasksRemoteDataSource, tasksLocalDataSource)
                    .apply { INSTANCE = this }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}

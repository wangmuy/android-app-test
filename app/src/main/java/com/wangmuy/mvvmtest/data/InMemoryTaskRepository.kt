package com.wangmuy.mvvmtest.data

import android.util.Log
import com.wangmuy.mvvmtest.Const.DEBUG_TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class InMemoryTaskRepository: TaskRepository {
    companion object {
        private const val TAG = "InMemoryTaskRepository$DEBUG_TAG"
    }

    private val _savedTasks = MutableStateFlow(LinkedHashMap<String, Task>())
    val savedTasks: StateFlow<LinkedHashMap<String, Task>> = _savedTasks.asStateFlow()

    private val observableTasks: Flow<List<Task>> = savedTasks.map {
        it.values.toList()
    }

    override fun getTasksStream(): Flow<List<Task>> = observableTasks

    override suspend fun createTask(title: String, description: String): String {
        val taskId = generateTaskId()
        Task(title = title, description = description, id = taskId).also {
            saveTask(it)
        }
        return taskId
    }

    private fun saveTask(task: Task) {
        Log.d(TAG, "saveTask task=$task")
        _savedTasks.update{tasks->
            val newTasks = LinkedHashMap<String, Task>(tasks)
            newTasks[task.id] = task
            newTasks
        }
    }

    private fun generateTaskId() = UUID.randomUUID().toString()
}
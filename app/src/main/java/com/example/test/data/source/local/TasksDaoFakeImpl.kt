package com.example.test.data.source.local

import com.example.test.data.model.Task

class TasksDaoFakeImpl: TasksDao {
    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    override fun getTaskList(): List<Task> {
        return ArrayList<Task>()
    }

    override fun getTask(taskId: String): Task? {
        val task = TASKS_SERVICE_DATA[taskId]
        return task
    }

    override fun saveTask(task: Task): Int {
        TASKS_SERVICE_DATA.put(task.id, task)
        return 1
    }

    override fun deleteTask(taskId: String): Int {
        val oldTask = TASKS_SERVICE_DATA.remove(taskId)
        return if (oldTask != null) 1 else 0
    }

    override fun deleteTasks() {
        TASKS_SERVICE_DATA.clear()
    }
}

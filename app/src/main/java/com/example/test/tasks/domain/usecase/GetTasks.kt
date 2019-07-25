package com.example.test.tasks.domain.usecase

import com.example.test.UseCase
import com.example.test.data.model.Task
import com.example.test.data.source.TasksDataSource
import com.example.test.data.source.TasksRepository

// TODO: 将 TasksLocalDataSource/TasksRemoteDataSource 等的线程调用放在这里
class GetTasks: UseCase<GetTasks.RequestValues, GetTasks.ResponseValue> {
    private val mTasksRepository: TasksRepository

    constructor(tasksRepository: TasksRepository) {
        mTasksRepository = tasksRepository
    }

    override fun executeUseCase(requestValues: RequestValues) {
        if (requestValues.isForceUpdate()) {
            mTasksRepository.refreshTasks()
        }

        mTasksRepository.getTaskList(object: TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val responseValue = ResponseValue(tasks)
                getUseCaseCallback().onSuccess(responseValue)
            }

            override fun onDataNotAvailable() {
                getUseCaseCallback().onError()
            }
        })
    }

    class RequestValues: UseCase.RequestValues {
        private val mForceUpdate: Boolean

        constructor(forceUpdate: Boolean) {
            mForceUpdate = forceUpdate
        }

        fun isForceUpdate(): Boolean {
            return mForceUpdate
        }
    }

    class ResponseValue: UseCase.ResponseValue {
        private val mTasks: List<Task>

        constructor(tasks: List<Task>) {
            mTasks = tasks
        }

        fun getTasks(): List<Task> {
            return mTasks
        }
    }
}
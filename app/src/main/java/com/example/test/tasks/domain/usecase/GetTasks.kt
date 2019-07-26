package com.example.test.tasks.domain.usecase

import com.example.test.UseCase
import com.example.test.data.model.Task
import com.example.test.data.source.TasksDataSource
import com.example.test.data.source.TasksRepository
import io.reactivex.Flowable

// TODO: 将 TasksLocalDataSource/TasksRemoteDataSource 等的线程调用放在这里
class GetTasks: UseCase<GetTasks.RequestValues, GetTasks.ResponseValue> {
    private val mTasksRepository: TasksRepository

    constructor(tasksRepository: TasksRepository) {
        mTasksRepository = tasksRepository
    }

    override fun executeUseCase(requestValues: RequestValues): ResponseValue {
        if (requestValues.isForceUpdate()) {
            mTasksRepository.refreshTasks()
        }

        return ResponseValue(mTasksRepository.getTaskList())
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
        private val mTasks: Flowable<List<Task>>

        constructor(tasks: Flowable<List<Task>>) {
            mTasks = tasks
        }

        fun getTasks(): Flowable<List<Task>> {
            return mTasks
        }
    }
}
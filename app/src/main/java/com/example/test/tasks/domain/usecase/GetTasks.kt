package com.example.test.tasks.domain.usecase

import com.example.test.UseCase
import com.example.test.tasks.data.model.Task
import com.example.test.tasks.data.source.TasksRepository
import io.reactivex.Flowable

class GetTasks(private val mTasksRepository: TasksRepository)
    : UseCase<GetTasks.RequestValues, GetTasks.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues): ResponseValue {
        if (requestValues.isForceUpdate()) {
            mTasksRepository.refreshTasks()
        }

        return ResponseValue(mTasksRepository.getTaskList())
    }

    class RequestValues(private val mForceUpdate: Boolean)
        : UseCase.RequestValues {
        fun isForceUpdate(): Boolean {
            return mForceUpdate
        }
    }

    class ResponseValue(private val mTasks: Flowable<List<Task>>)
        : UseCase.ResponseValue {
        fun getTasks(): Flowable<List<Task>> {
            return mTasks
        }
    }
}

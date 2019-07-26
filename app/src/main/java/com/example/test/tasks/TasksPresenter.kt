package com.example.test.tasks

import com.example.test.UseCase
import com.example.test.data.model.Task
import com.example.test.data.source.TasksRepository
import com.example.test.tasks.domain.usecase.GetTasks

class TasksPresenter(
        val tasksView: TasksContract.View,
        val getTasks: GetTasks): TasksContract.Presenter {

    private var firstLoad = true

    init {
        tasksView.presenter = this
    }

    override fun start() {
        loadTasks(false)
    }

    override fun loadTasks(forceUpdate: Boolean) {
        loadTasks(forceUpdate || firstLoad, true)
        firstLoad = false
    }

    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true)
        }

        val requestValues = GetTasks.RequestValues(forceUpdate)
        getTasks.setRequestValues(requestValues)
        getTasks.setUseCaseCallback(object: UseCase.UseCaseCallback<GetTasks.ResponseValue> {
            override fun onSuccess(response: GetTasks.ResponseValue) {
                val tasks = response.getTasks()
                val tasksToShow = ArrayList<Task>()
                tasksToShow.addAll(tasks)
                if (!tasksView.isActive) {
                    return
                }
                if (showLoadingUI) {
                    tasksView.setLoadingIndicator(false)
                }

                if (tasksToShow.isEmpty()) {
                    tasksView.showNoTasks()
                } else {
                    tasksView.showTasks(tasksToShow)
                }
            }

            override fun onError() {
                if (!tasksView.isActive) {
                    return
                }
                tasksView.showLoadingTasksError()
            }
        })
        getTasks.executeUseCase(requestValues)
    }
}

package com.example.test.tasks

import com.example.test.UseCase
import com.example.test.data.model.Task
import com.example.test.data.source.TasksRepository
import com.example.test.tasks.domain.usecase.GetTasks
import io.reactivex.disposables.CompositeDisposable

class TasksPresenter(
        val tasksView: TasksContract.View,
        val getTasks: GetTasks): TasksContract.Presenter {

    private var firstLoad = true
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        tasksView.presenter = this
    }

    override fun subscribe() {
        loadTasks(false)
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
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
        val disposable = getTasks.executeUseCase(requestValues).getTasks()
                .subscribe(
                        // onNext
                        {
                            if (tasksView.isActive) {
                                if (showLoadingUI) {
                                    tasksView.setLoadingIndicator(false)
                                }

                                if (it.isNotEmpty()) {
                                    val tasksToShow = ArrayList<Task>()
                                    tasksToShow.addAll(it)
                                    tasksView.showTasks(tasksToShow)
                                } else {
                                    tasksView.showNoTasks()
                                }
                            }
                        },
                        // onError
                        {
                            if (tasksView.isActive) {
                                tasksView.showLoadingTasksError()
                            }
                        }
                )
        compositeDisposable.add(disposable)
    }
}

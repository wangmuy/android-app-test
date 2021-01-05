package com.example.test.tasks

import android.util.Log
import com.example.test.tasks.data.model.Task
import com.example.test.tasks.domain.usecase.GetTasks
import com.example.test.util.schedulers.BaseSchedulerProvider
import io.reactivex.disposables.CompositeDisposable

private const val TAG = "TasksPresenter"
class TasksPresenter(
        private val tasksView: TasksContract.View,
        private val getTasks: GetTasks,
        private val schedulerProvider: BaseSchedulerProvider)
    : TasksContract.Presenter {

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
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        // onNext
                        { tasks ->
                            Log.d(TAG, "subscribe tid=" + Thread.currentThread().id)
                            if (tasksView.isActive) {
                                if (showLoadingUI) {
                                    tasksView.setLoadingIndicator(false)
                                }

                                if (tasks.isNotEmpty()) {
                                    val tasksToShow = ArrayList<Task>()
                                    tasksToShow.addAll(tasks)
                                    tasksView.showTasks(tasksToShow)
                                } else {
                                    tasksView.showNoTasks()
                                }
                            }
                        },
                        // onError
                        { throwable ->
                            if (tasksView.isActive) {
                                tasksView.showLoadingTasksError(throwable.toString())
                            }
                        }
                )
        compositeDisposable.add(disposable)
    }
}

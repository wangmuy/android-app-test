package com.example.test.tasks

import com.example.test.BasePresenter
import com.example.test.BaseView
import com.example.test.data.model.Task

interface TasksContract {

    interface Presenter: BasePresenter {
        fun loadTasks(forceUpdate: Boolean)
    }

    interface View: BaseView<Presenter> {
        var isActive: Boolean
        fun setLoadingIndicator(active: Boolean)
        fun showTasks(tasks: List<Task>)
        fun showNoTasks()
        fun showLoadingTasksError()
    }
}

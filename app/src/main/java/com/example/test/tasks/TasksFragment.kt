package com.example.test.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.test.R
import com.example.test.data.model.Task

class TasksFragment: Fragment(), TasksContract.View {
    private val TAG = "TasksFragment"

    override lateinit var presenter: TasksContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    private lateinit var noTasksView: View
    private lateinit var tasksView: ViewGroup

    internal var itemListener: TaskItemListener = object: TaskItemListener {
        override fun onTaskClick(clickedTask: Task) {
            Log.d(TAG, "task clicked: $clickedTask")
        }
    }

    private val listAdapter = TasksAdapter(ArrayList(0), itemListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.tasks_frag, container, false)
        with (root) {
            val listView = findViewById<ListView>(R.id.tasks_list).apply { adapter = listAdapter }
            tasksView = findViewById(R.id.tasksLL)
            noTasksView = findViewById(R.id.noTasks)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onStop() {
        super.onStop()
        presenter.unsubscribe()
    }

    override fun setLoadingIndicator(active: Boolean) {
        Log.d(TAG, "setLoadingIndicator: $active")
    }

    override fun showTasks(tasks: List<Task>) {
        listAdapter.tasks = tasks
        tasksView.visibility = View.VISIBLE
        noTasksView.visibility = View.GONE
    }

    override fun showNoTasks() {
        tasksView.visibility = View.GONE
        noTasksView.visibility = View.VISIBLE
    }

    override fun showLoadingTasksError() {
        Toast.makeText(this.context, "Error while loading tasks", Toast.LENGTH_SHORT).show()
    }

    private class TasksAdapter(tasks: List<Task>, private val itemListener: TaskItemListener)
        : BaseAdapter() {
        var tasks: List<Task> = tasks
            set(tasks) {
                field = tasks
                notifyDataSetChanged()
            }

        override fun getCount() = tasks.size

        override fun getItem(i: Int) = tasks[i]

        override fun getItemId(i: Int) = i.toLong()

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val task = getItem(i)
            val rowView = view ?: LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.task_item, viewGroup, false)

            with (rowView.findViewById<TextView>(R.id.title)) {
                text = task.titleForList
            }
            rowView.setOnClickListener { itemListener.onTaskClick(task) }
            return rowView
        }
    }

    interface TaskItemListener {
        fun onTaskClick(clickedTask: Task)
    }

    companion object {
        fun newInstance() = TasksFragment()
    }
}

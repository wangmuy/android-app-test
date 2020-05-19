package com.example.test.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.data.model.Task

private const val TAG = "TasksFragment"

class TasksFragment: Fragment(), TasksContract.View {

    override lateinit var presenter: TasksContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    private lateinit var progressBar: View
    private lateinit var noTasksView: View
    private lateinit var tasksView: ViewGroup

    internal var itemListener: TaskItemListener = object: TaskItemListener {
        override fun onTaskClick(clickedTask: Task) {
            Log.d(TAG, "task clicked: $clickedTask")
        }
    }

    private val recyclerAdapter = TasksAdapter(ArrayList(0), itemListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.tasks_frag, container, false)
        with (root) {
            val recyclerView = findViewById<RecyclerView>(R.id.tasks_list)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = recyclerAdapter
            }
            tasksView = findViewById(R.id.tasksLL)
            noTasksView = findViewById(R.id.noTasks)
            progressBar = findViewById(R.id.progressBar)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume tid=" + Thread.currentThread().id)
        presenter.subscribe()
    }

    override fun onStop() {
        super.onStop()
        presenter.unsubscribe()
    }

    override fun setLoadingIndicator(active: Boolean) {
         progressBar.visibility = if (active) View.VISIBLE else View.INVISIBLE
    }

    override fun showTasks(tasks: List<Task>) {
        recyclerAdapter.tasks = tasks
        tasksView.visibility = View.VISIBLE
        noTasksView.visibility = View.GONE
    }

    override fun showNoTasks() {
        tasksView.visibility = View.GONE
        noTasksView.visibility = View.VISIBLE
    }

    override fun showLoadingTasksError(errStr: String) {
        Toast.makeText(this.context, "Error while loading tasks: $errStr", Toast.LENGTH_SHORT).show()
    }

    private class TasksAdapter(tasks: List<Task>, private val itemListener: TaskItemListener)
        : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {
        var tasks: List<Task> = tasks
            set(tasks) {
                field = tasks
                notifyDataSetChanged()
            }

        override fun getItemCount() = tasks.size

        override fun getItemId(i: Int) = i.toLong()

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_item, parent, false)
            return ViewHolder(itemView, itemListener)
        }

        private class ViewHolder(itemView: View, val itemListener: TaskItemListener)
            : RecyclerView.ViewHolder(itemView) {
            lateinit var task: Task
            val title: TextView

            init {
                title = itemView.findViewById(R.id.title)
                itemView.setOnClickListener { itemListener.onTaskClick(task) }
            }

            fun bind(task: Task) {
                this.task = task
                title.text = task.titleForList
            }
        }
    }

    interface TaskItemListener {
        fun onTaskClick(clickedTask: Task)
    }

    companion object {
        fun newInstance() = TasksFragment()
    }
}

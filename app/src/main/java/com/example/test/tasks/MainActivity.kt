package com.example.test.tasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.Injection
import com.example.test.R
import com.example.test.util.replaceFragmentInActivity

class MainActivity: AppCompatActivity() {
    private lateinit var tasksPresenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
            as TasksFragment? ?: TasksFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        tasksPresenter = TasksPresenter(
                tasksFragment,
                Injection.provideUseCaseGetTasks(applicationContext),
                Injection.providerSchedulerProvider()).apply {
            // empty
        }
    }
}

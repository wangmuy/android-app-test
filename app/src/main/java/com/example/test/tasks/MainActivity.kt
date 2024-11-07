package com.example.test.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.test.Injection
import com.example.test.R
import com.example.test.util.SystemPropertiesUtil
import com.example.test.util.replaceFragmentInActivity

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivityTAG"
    }
    private lateinit var tasksPresenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate intent=$intent")
        setContentView(R.layout.activity_main)

        if (intent.hasExtra(":aicaption:action_type")) {
            val resultCode = SystemPropertiesUtil.getInt("ga.ar", -1)
            Log.d(TAG, "setResult and finish resultCode=$resultCode")
            setResult(resultCode)
            finish()
        }

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

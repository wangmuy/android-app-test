package com.wangmuy.mvvmtest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wangmuy.mvvmtest.Const.DEBUG_TAG
import com.wangmuy.mvvmtest.data.InMemoryTaskRepository
import com.wangmuy.mvvmtest.ui.tasks.TasksScreen
import com.wangmuy.mvvmtest.ui.tasks.TasksViewModel
import com.wangmuy.mvvmtest.ui.theme.MVVMTestTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity$DEBUG_TAG"
        private val sTasksRepository by lazy { InMemoryTaskRepository() }
    }

    private val mViewModel = TasksViewModel(sTasksRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVVMTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TasksScreen(
                        viewModel = mViewModel,
                        userMessage = "todo test",
                        onAddTask = {
                            Log.d(TAG, "onAddTask")
                            mViewModel.createNewTask()
                        })
                }
            }
        }
    }
}

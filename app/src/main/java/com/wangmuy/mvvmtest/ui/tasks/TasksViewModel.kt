package com.wangmuy.mvvmtest.ui.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wangmuy.mvvmtest.Const.DEBUG_TAG
import com.wangmuy.mvvmtest.data.Task
import com.wangmuy.mvvmtest.data.TaskRepository
import com.wangmuy.mvvmtest.util.Async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class TasksUiState(
    val items: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

class TasksViewModel constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
    companion object {
        private const val TAG = "TasksViewModel$DEBUG_TAG"
        private const val StopTimeoutMillis: Long = 5000
    }

    private val _isLoading = MutableStateFlow(false)
    private val _tasksAsync = taskRepository.getTasksStream()
        .map { Async.Success(it) }
        .catch<Async<List<Task>>> { emit(Async.Error("Error while loading tasks")) }

    val uiState: StateFlow<TasksUiState> = combine(_isLoading, _tasksAsync) {isLoading, tasksAsync->
        when(tasksAsync) {
            Async.Loading -> {
                TasksUiState(isLoading = true)
            }
            is Async.Error -> {
                TasksUiState(userMessage = tasksAsync.errorMessage)
            }
            is Async.Success -> {
                TasksUiState(items = tasksAsync.data, isLoading = isLoading)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(StopTimeoutMillis),
        initialValue = TasksUiState(isLoading = true)
    )

    fun createNewTask() = viewModelScope.launch {
        val suffix = UUID.randomUUID().toString().substring(0, 5)
        Log.d(TAG, "createNewTask: $suffix")
        taskRepository.createTask("title$suffix", "desc$suffix")
    }
}
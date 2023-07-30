package com.wangmuy.mvvmtest.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.wangmuy.mvvmtest.data.Task

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    userMessage: String,
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column {
        Button(onClick = onAddTask) {
            Text(text = "newTask")
        }
        TasksContent(
            loading = uiState.isLoading,
            tasks = uiState.items,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun TasksContent(
    loading: Boolean,
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(tasks) {task->
            TaskItem(task = task)
        }
    }
}

@Composable
private fun TaskItem(
    task: Task
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Checkbox(checked = task.isCompleted, onCheckedChange = null)
        Text(
            text = task.titleForList,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp),
            textDecoration = if (task.isCompleted) {
                TextDecoration.LineThrough
            } else { null }
        )
    }
}
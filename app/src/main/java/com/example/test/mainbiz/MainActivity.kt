package com.example.test.mainbiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.common.ui.theme.TestAppTheme
import com.example.test.mainbiz.presentation.MainViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }
}

@Composable
fun MainView(viewModel: MainViewModel = koinViewModel()) {
    val shellOutput by viewModel.shellOutput.collectAsState()
    val isShellRunning by viewModel.isShellRunning.collectAsState()
    val isCommandExecuting by viewModel.isCommandExecuting.collectAsState()
    var commandText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { viewModel.startShell() },
                enabled = !isShellRunning,
                modifier = Modifier.weight(1f)
            ) {
                Text("Start sh")
            }

            OutlinedButton(
                onClick = { viewModel.stopShell() },
                enabled = isShellRunning,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Stop sh")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = commandText,
                onValueChange = { commandText = it },
                modifier = Modifier.weight(1f),
                enabled = isShellRunning,
                placeholder = { Text("Enter command") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        viewModel.executeShellCommand(commandText)
                        commandText = ""
                    }
                )
            )

            Button(
                onClick = {
                    viewModel.executeShellCommand(commandText)
                    commandText = ""
                },
                enabled = isShellRunning && commandText.isNotBlank() && !isCommandExecuting,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Execute")
            }

            OutlinedButton(
                onClick = { viewModel.clearShellOutput() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Clear")
            }
        }

        Text(
            text = "Output:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            OutlinedTextField(
                value = shellOutput,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                textStyle = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestAppTheme {
        MainView()
    }
}

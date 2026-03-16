package com.example.test.mainbiz.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.mainbiz.util.ShellExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
) : ViewModel() {

    private val _shellOutput = MutableStateFlow("")
    val shellOutput: StateFlow<String> = _shellOutput.asStateFlow()

    private val _isShellRunning = MutableStateFlow(false)
    val isShellRunning: StateFlow<Boolean> = _isShellRunning.asStateFlow()

    private val _isCommandExecuting = MutableStateFlow(false)
    val isCommandExecuting: StateFlow<Boolean> = _isCommandExecuting.asStateFlow()

    private var shellExecutor: ShellExecutor? = null

    fun startShell() {
        shellExecutor = ShellExecutor(
            onOutput = { output ->
                viewModelScope.launch {
                    _shellOutput.value += output + "\n"
                }
            },
            onError = { error ->
                viewModelScope.launch {
                    _shellOutput.value += "[stderr] $error\n"
                }
            },
            onCommandDone = { exitCode ->
                viewModelScope.launch {
                    _shellOutput.value += "Exit code: $exitCode\n"
                    _isCommandExecuting.value = false
                }
            }
        )

        if (shellExecutor?.startShell() == true) {
            _isShellRunning.value = true
            _shellOutput.value += "Shell started\n"
        } else {
            _shellOutput.value += "[Failed to start shell]\n"
        }
    }

    fun stopShell() {
        shellExecutor?.stopShell()
        shellExecutor = null
        _isShellRunning.value = false
        _isCommandExecuting.value = false
        _shellOutput.value += "Shell stopped\n"
    }

    fun executeShellCommand(command: String) {
        if (command.isNotBlank() && !_isCommandExecuting.value) {
            _shellOutput.value += "$ $command\n"
            _isCommandExecuting.value = true
            shellExecutor?.sendCommand(command)
        }
    }

    fun clearShellOutput() {
        _shellOutput.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        shellExecutor?.stopShell()
    }
}

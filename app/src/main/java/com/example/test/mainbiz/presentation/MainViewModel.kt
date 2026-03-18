package com.example.test.mainbiz.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.mainbiz.util.Logger
import com.example.test.mainbiz.util.ShellCallback
import com.example.test.mainbiz.util.ShellExecutor
import com.example.test.mainbiz.util.UnixSocketServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context? = null
) : ViewModel(), ShellCallback {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _shellOutput = MutableStateFlow("")
    val shellOutput: StateFlow<String> = _shellOutput.asStateFlow()

    private val _isShellRunning = MutableStateFlow(false)
    val isShellRunning: StateFlow<Boolean> = _isShellRunning.asStateFlow()

    private val _isCommandExecuting = MutableStateFlow(false)
    val isCommandExecuting: StateFlow<Boolean> = _isCommandExecuting.asStateFlow()

    private var shellExecutor: ShellExecutor? = null
    private var socketServer: UnixSocketServer? = null

    fun startShell(bindMounts: List<String> = emptyList()) {
        viewModelScope.launch(Dispatchers.IO) {
            context?.let { ctx ->
                socketServer = UnixSocketServer(
                    ctx,
                    this@MainViewModel,
                    object: Logger {
                    override fun log(tag: String, message: String) {
                        Log.d(TAG, message)
                        viewModelScope.launch {
                            val msg = "$tag: $message\n"
                            _shellOutput.value += msg
                        }
                    }
                })
                socketServer?.startServer()

                val scriptDir = ctx.filesDir.absolutePath
                shellExecutor = ShellExecutor(
                    ctx,
                    scriptDir,
                    bindMounts,
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
        }
    }

    fun stopShell() {
        viewModelScope.launch(Dispatchers.IO) {
            shellExecutor?.stopShell()
            shellExecutor = null

            socketServer?.stopServer()
            socketServer = null

            _isShellRunning.value = false
            _isCommandExecuting.value = false
            _shellOutput.value += "Shell stopped\n"
        }
    }

    fun executeShellCommand(command: String) {
        if (command.isNotBlank() && !_isCommandExecuting.value) {
            viewModelScope.launch(Dispatchers.IO) {
                _shellOutput.value += "$ $command\n"
                _isCommandExecuting.value = true
                shellExecutor?.sendCommand(command)
            }
        }
    }

    fun clearShellOutput() {
        _shellOutput.value = ""
    }

    override fun call(request: String): Pair<Int, String> {
        val firstWord = request.trim().split("\\s+".toRegex()).firstOrNull() ?: ""
        return if (firstWord == "ok") {
            callSuccess(request)
        } else {
            callFail(request)
        }
    }

    private fun callSuccess(request: String): Pair<Int, String> {
        return Pair(0, "Success: processed '$request'")
    }

    private fun callFail(request: String): Pair<Int, String> {
        return Pair(1, "Failed: '$request' is not recognized")
    }

    override fun onCleared() {
        super.onCleared()
        shellExecutor?.stopShell()
        socketServer?.stopServer()
    }
}

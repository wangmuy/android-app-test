package com.example.test.mainbiz.util

interface ShellCallback {
    fun call(request: String): Pair<Int, String>
}

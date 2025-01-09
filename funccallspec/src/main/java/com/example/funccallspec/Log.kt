package com.example.funccallspec

internal object Log {
    fun d(tag: String,  msg: String): String {
        val msg = "$tag: $msg"
        println(msg)
        return msg
    }
}
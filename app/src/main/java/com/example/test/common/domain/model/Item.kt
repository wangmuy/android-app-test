package com.example.test.domain.model

data class Item(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val createdAt: Long = System.currentTimeMillis()
)
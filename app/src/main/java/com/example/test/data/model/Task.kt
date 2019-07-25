package com.example.test.data.model

import java.util.*

/**
 * Model class for a Task
 */
data class Task @JvmOverloads constructor(
        var title: String = "",
        var description: String = "",
        var id: String = UUID.randomUUID().toString()
) {
    var isCompleted = false

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() && description.isEmpty()
}

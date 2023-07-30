package com.wangmuy.mvvmtest.data

data class Task(
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val id: String
) {
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()

    override fun toString(): String {
        return "Task(title='$title', description='$description', isCompleted=$isCompleted, id='$id')"
    }
}
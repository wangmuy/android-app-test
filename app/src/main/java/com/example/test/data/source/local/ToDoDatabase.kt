package com.example.test.data.source.local

import android.content.Context

abstract class ToDoDatabase {
    abstract fun taskDao(): TasksDao

    companion object {
        private var INSTANCE: ToDoDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): ToDoDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    context.packageName
                    INSTANCE = object: ToDoDatabase() {
                        override fun taskDao(): TasksDao {
                            return TasksDaoFakeImpl()
                        }
                    }
                }
                return INSTANCE!!
            }
        }
    }
}
package com.example.test.common.data.repository

import com.example.test.domain.model.Item
import kotlinx.coroutines.flow.Flow
import com.example.test.util.Result

interface ItemRepository {
    fun getItems(): Flow<Result<List<Item>>>
    fun getItem(itemId: String): Flow<Result<Item?>>
    suspend fun refreshItems()
    suspend fun updateItem(item: Item): Result<Unit>
    suspend fun refreshItem(itemId: String): Result<Unit>
    suspend fun deleteItem(item: Item): Result<Unit>
}
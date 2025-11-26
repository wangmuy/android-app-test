package com.example.test.common.data.repository

import com.example.test.common.data.api.ItemApiService
import com.example.test.common.data.local.dao.ItemDao
import com.example.test.common.data.local.entity.toApiModel
import com.example.test.common.data.local.entity.toDomainModel
import com.example.test.common.data.local.entity.toEntity
import com.example.test.domain.model.Item
import com.example.test.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers

class ItemRepositoryImpl(
    private val apiService: ItemApiService? = null,
    private val itemDao: ItemDao? = null
) : ItemRepository {

    override fun getItems(): Flow<Result<List<Item>>> = flow {
        // First emit cached data if DAO is available
        itemDao?.let { dao ->
            dao.getAllItems().collect { entities ->
                if (entities.isNotEmpty()) {
                    emit(Result.Success(entities.map { it.toDomainModel() }))
                }
            }
        }

        // Then refresh from network if API service is available
        apiService?.let { service ->
            try {
                val apiItems = service.getItems()
                val entities = apiItems.map { it.toEntity() }
                itemDao?.insertItems(entities) // Only insert if DAO is available
                emit(Result.Success(entities.map { it.toDomainModel() }))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        } ?: run {
            // If no API service, just emit what we have in DAO
            if (itemDao == null) {
                // If both are null, return empty list
                emit(Result.Success(emptyList()))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getItem(itemId: String): Flow<Result<Item?>> = flow {
        // First emit cached data if DAO is available
        itemDao?.let { dao ->
            dao.getItemById(itemId).collect { entity ->
                emit(Result.Success(entity?.toDomainModel()))
            }
        }

        // Then refresh from network if API service is available
        apiService?.let { service ->
            try {
                val apiItem = service.getItem(itemId)
                val entity = apiItem.toEntity()
                itemDao?.insertItem(entity) // Only insert if DAO is available
                emit(Result.Success(entity.toDomainModel()))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        } ?: run {
            // If no API service, just emit what we have in DAO
            if (itemDao == null) {
                // If both are null, return null
                emit(Result.Success(null))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refreshItems() {
        apiService?.let { service ->
            try {
                val apiItems = service.getItems()
                val entities = apiItems.map { it.toEntity() }
                itemDao?.insertItems(entities) // Only insert if DAO is available
            } catch (e: Exception) {
                // Log error but don't crash
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateItem(item: Item): Result<Unit> {
        return try {
            val apiModel = item.toApiModel()
            // Update both API and local DB if available
            apiService?.updateItem(item.id, apiModel)
            itemDao?.updateItem(item.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refreshItem(itemId: String): Result<Unit> {
        return if (apiService != null) {
            try {
                val apiItem = apiService.getItem(itemId)
                val entity = apiItem.toEntity()
                itemDao?.insertItem(entity) // Only insert if DAO is available
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        } else {
            Result.Error(Exception("API Service not available"))
        }
    }

    override suspend fun deleteItem(item: Item): Result<Unit> {
        return try {
            // Delete from both API and local DB if available
            apiService?.deleteItem(item.id)
            itemDao?.deleteItem(item.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
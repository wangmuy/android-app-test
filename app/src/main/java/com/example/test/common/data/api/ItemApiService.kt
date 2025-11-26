package com.example.test.common.data.api

import com.example.test.common.data.api.model.ItemApiModel
import retrofit2.http.*

interface ItemApiService {
    @GET("items")
    suspend fun getItems(): List<ItemApiModel>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") userId: String): ItemApiModel

    @POST("items")
    suspend fun createUser(@Body user: ItemApiModel): ItemApiModel

    @PUT("items/{id}")
    suspend fun updateItem(@Path("id") userId: String, @Body user: ItemApiModel): ItemApiModel

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") userId: String)
}
package com.wangmuy.testempty

interface IRepo {
    suspend fun getAll(): List<Item>
    suspend fun get(id: String): Item?
}
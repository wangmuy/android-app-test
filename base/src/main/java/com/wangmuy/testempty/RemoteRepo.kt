package com.wangmuy.testempty

class RemoteRepo(val repoName: String): IRepo {
    private val items = listOf(Item("remoteItem1"), Item("remoteItem2"))

    override suspend fun getAll(): List<Item> {
        return items
    }

    override suspend fun get(id: String): Item? {
        return items.firstOrNull { it.name == id }
    }
}
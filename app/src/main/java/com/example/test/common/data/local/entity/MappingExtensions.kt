package com.example.test.common.data.local.entity

import com.example.test.domain.model.Item
import com.example.test.common.data.api.model.ItemApiModel

// Entity to Domain
fun ItemEntity.toDomainModel(): Item = Item(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)

// API Model to Entity
fun ItemApiModel.toEntity(): ItemEntity = ItemEntity(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    createdAt = System.currentTimeMillis()
)

// Domain to API Model
fun Item.toApiModel(): ItemApiModel = ItemApiModel(
    id = id,
    name = name,
    avatarUrl = avatarUrl
)

// Domain to Entity
fun Item.toEntity(): ItemEntity = ItemEntity(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)
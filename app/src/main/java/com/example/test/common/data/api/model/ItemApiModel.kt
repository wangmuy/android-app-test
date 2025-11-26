package com.example.test.common.data.api.model

import com.google.gson.annotations.SerializedName

data class ItemApiModel(
    val id: String,
    val name: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)
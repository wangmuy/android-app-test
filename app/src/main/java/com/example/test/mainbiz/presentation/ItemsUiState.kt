package com.example.test.mainbiz.presentation

import com.example.test.domain.model.Item

sealed interface ItemsUiState {
    data object Loading : ItemsUiState
    data class Success(val items: List<Item>) : ItemsUiState
    data class Error(val message: String) : ItemsUiState

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error
}
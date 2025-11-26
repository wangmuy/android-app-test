package com.example.test.mainbiz.presentation

sealed interface ItemsUiEffect {
    data class NavigateToItemDetail(val userId: String) : ItemsUiEffect
}
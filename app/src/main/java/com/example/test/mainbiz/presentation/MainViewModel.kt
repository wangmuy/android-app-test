package com.example.test.mainbiz.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.common.data.repository.ItemRepository
import com.example.test.domain.model.Item
import com.example.test.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    companion object {
        private val SAMPLE_TEXT = """
        This is a sample multiline text using Jetpack Compose.
        It demonstrates how to display multiple lines of text in an Android application.
        The text will automatically wrap to fit the screen width.
        This shows the basic usage of Compose for UI rendering.
    """
    }

    private val _uiState = MutableStateFlow<ItemsUiState>(ItemsUiState.Loading)
    val uiState: StateFlow<ItemsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ItemsUiEffect>()
    val uiEffect: SharedFlow<ItemsUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = ItemsUiState.Loading

            itemRepository.getItems()
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data.isEmpty()) {
                                _uiState.value = ItemsUiState.Error("No items found")
                            } else {
                                _uiState.value = ItemsUiState.Success(result.data)
                            }
                        }
                        is Result.Error -> {
                            _uiState.value = ItemsUiState.Error(
                                result.exception.message ?: "Unknown error"
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onItemClick(item: Item) {
        viewModelScope.launch {
            _uiEffect.emit(ItemsUiEffect.NavigateToItemDetail(item.id))
        }
    }

    fun refreshItems() {
        viewModelScope.launch {
            itemRepository.refreshItems()
        }
    }

    private val _textContent = MutableStateFlow(SAMPLE_TEXT.trimIndent())

    val textContent: StateFlow<String> = _textContent.asStateFlow()

    fun updateText(newText: String) {
        viewModelScope.launch {
            _textContent.value = newText
        }
    }

    fun resetText() {
        viewModelScope.launch {
            _textContent.value = SAMPLE_TEXT.trimIndent()
        }
    }

    fun onMyButtonClicked() {
        viewModelScope.launch {
            val words = listOf(
                "apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honeydew"
            )

            val random = kotlin.random.Random.Default
            val word1 = words[random.nextInt(words.size)]
            val word2 = words[random.nextInt(words.size)]
            val word3 = words[random.nextInt(words.size)]

            _textContent.value = "$word1 $word2 $word3"
        }
    }
}
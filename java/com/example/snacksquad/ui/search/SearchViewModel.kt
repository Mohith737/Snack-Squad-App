package com.example.snacksquad.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snacksquad.data.model.FoodItem
import com.example.snacksquad.data.repository.FoodRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class)
class SearchViewModel(
    foodRepository: FoodRepository
) : ViewModel() {
    private val allFoodItems = foodRepository.getFoodItems()
    private val _query = MutableStateFlow("")

    val query: StateFlow<String> = _query.asStateFlow()
    val filteredResults: StateFlow<List<FoodItem>> = _query
        .debounce(debounceDurationMillis)
        .map { currentQuery ->
            allFoodItems.filter { foodItem ->
                foodItem.name.contains(currentQuery, ignoreCase = true)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = allFoodItems
        )

    fun updateQuery(query: String) {
        _query.value = query
    }

    companion object {
        @Volatile
        var debounceDurationMillis: Long = 300L

        fun provideFactory(foodRepository: FoodRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(foodRepository) as T
                }
            }
        }
    }
}

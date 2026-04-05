package com.example.snacksquad.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snacksquad.data.model.FoodItem
import com.example.snacksquad.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    foodRepository: FoodRepository
) : ViewModel() {
    private val _popularItems = MutableStateFlow(foodRepository.getFoodItems())
    val popularItems: StateFlow<List<FoodItem>> = _popularItems.asStateFlow()

    companion object {
        fun provideFactory(foodRepository: FoodRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(foodRepository) as T
                }
            }
        }
    }
}

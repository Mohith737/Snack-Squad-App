package com.example.snacksquad.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snacksquad.data.model.OrderHistory
import com.example.snacksquad.data.repository.OrderHistoryRepository
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel(
    orderHistoryRepository: OrderHistoryRepository
) : ViewModel() {
    val orderHistories: StateFlow<List<OrderHistory>> = orderHistoryRepository.orderHistories

    companion object {
        fun provideFactory(orderHistoryRepository: OrderHistoryRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel(orderHistoryRepository) as T
                }
            }
        }
    }
}

package com.example.snacksquad.data.repository

import android.content.Context
import com.example.snacksquad.data.model.CartItem

object RepositoryProvider {
    val foodRepository: FoodRepository by lazy { FoodRepository() }
    private val defaultCartItems: List<CartItem>
        get() = foodRepository.getFoodItems().map { foodItem ->
            CartItem(foodItem = foodItem, quantity = 1)
        }

    val cartRepository: CartRepository by lazy { CartRepository(defaultCartItems) }

    @Volatile
    private var orderHistoryRepository: OrderHistoryRepository? = null

    fun getOrderHistoryRepository(context: Context): OrderHistoryRepository {
        return orderHistoryRepository ?: synchronized(this) {
            orderHistoryRepository ?: OrderHistoryRepository(context.applicationContext).also {
                orderHistoryRepository = it
            }
        }
    }

    fun resetCartForTests() {
        cartRepository.replaceCartItems(defaultCartItems)
    }

    fun clearOrderHistoryForTests(context: Context) {
        getOrderHistoryRepository(context).clearOrderHistory()
    }
}

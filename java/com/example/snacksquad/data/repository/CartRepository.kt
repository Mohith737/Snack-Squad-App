package com.example.snacksquad.data.repository

import com.example.snacksquad.data.model.CartItem
import com.example.snacksquad.data.model.FoodItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartRepository(
    initialItems: List<CartItem> = emptyList()
) {
    private val _cartItems = MutableStateFlow(initialItems)
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(item: FoodItem) {
        _cartItems.update { currentItems ->
            val existingIndex = currentItems.indexOfFirst { it.foodItem.id == item.id }
            if (existingIndex == -1) {
                currentItems + CartItem(foodItem = item, quantity = 1)
            } else {
                currentItems.toMutableList().apply {
                    val existingItem = this[existingIndex]
                    this[existingIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
                }
            }
        }
    }

    fun removeFromCart(position: Int) {
        _cartItems.update { currentItems ->
            if (position !in currentItems.indices) {
                currentItems
            } else {
                currentItems.toMutableList().apply {
                    removeAt(position)
                }
            }
        }
    }

    fun increaseQuantity(position: Int) {
        _cartItems.update { currentItems ->
            if (position !in currentItems.indices) {
                currentItems
            } else {
                currentItems.toMutableList().apply {
                    val cartItem = this[position]
                    this[position] = cartItem.copy(quantity = cartItem.quantity + 1)
                }
            }
        }
    }

    fun decreaseQuantity(position: Int) {
        _cartItems.update { currentItems ->
            if (position !in currentItems.indices) {
                currentItems
            } else {
                currentItems.toMutableList().apply {
                    val cartItem = this[position]
                    if (cartItem.quantity > 1) {
                        this[position] = cartItem.copy(quantity = cartItem.quantity - 1)
                    }
                }
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun replaceCartItems(items: List<CartItem>) {
        _cartItems.value = items
    }
}

package com.example.snacksquad.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snacksquad.data.model.CartItem
import com.example.snacksquad.data.model.FoodItem
import com.example.snacksquad.data.repository.CartRepository
import com.example.snacksquad.data.repository.OrderHistoryRepository
import kotlinx.coroutines.flow.StateFlow

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderHistoryRepository: OrderHistoryRepository
) : ViewModel() {
    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems

    fun addToCart(item: FoodItem) {
        cartRepository.addToCart(item)
    }

    fun removeFromCart(position: Int) {
        cartRepository.removeFromCart(position)
    }

    fun increaseQuantity(position: Int) {
        cartRepository.increaseQuantity(position)
    }

    fun decreaseQuantity(position: Int) {
        cartRepository.decreaseQuantity(position)
    }

    fun getTotalPrice(): Double {
        return calculateTotalPrice(cartItems.value)
    }

    fun placeOrder(cartItems: List<CartItem>) {
        if (cartItems.isEmpty()) {
            return
        }
        orderHistoryRepository.placeOrder(cartItems, calculateTotalPrice(cartItems))
        cartRepository.clearCart()
    }

    private fun calculateTotalPrice(cartItems: List<CartItem>): Double {
        return cartItems.sumOf { cartItem ->
            val itemPrice = cartItem.foodItem.price
                .filter { it.isDigit() || it == '.' }
                .toDoubleOrNull()
                ?: 0.0
            itemPrice * cartItem.quantity
        }
    }

    companion object {
        fun provideFactory(
            cartRepository: CartRepository,
            orderHistoryRepository: OrderHistoryRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CartViewModel(cartRepository, orderHistoryRepository) as T
                }
            }
        }
    }
}

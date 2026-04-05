package com.example.snacksquad.data.model

data class OrderHistory(
    val id: Long,
    val items: List<CartItem>,
    val totalPrice: Double,
    val timestamp: Long
)

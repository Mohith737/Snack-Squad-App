package com.example.snacksquad.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.snacksquad.data.model.CartItem
import com.example.snacksquad.data.model.FoodItem
import com.example.snacksquad.data.model.OrderHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class OrderHistoryRepository(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _orderHistories = MutableStateFlow(loadOrderHistories())
    val orderHistories: StateFlow<List<OrderHistory>> = _orderHistories.asStateFlow()

    fun placeOrder(cartItems: List<CartItem>, totalPrice: Double) {
        if (cartItems.isEmpty()) {
            return
        }

        val timestamp = System.currentTimeMillis()
        val orderHistory = OrderHistory(
            id = timestamp,
            items = cartItems,
            totalPrice = totalPrice,
            timestamp = timestamp
        )
        val updatedOrders = listOf(orderHistory) + _orderHistories.value
        saveOrderHistories(updatedOrders)
        _orderHistories.value = updatedOrders
    }

    fun clearOrderHistory() {
        preferences.edit().remove(KEY_ORDER_HISTORY).apply()
        _orderHistories.value = emptyList()
    }

    private fun loadOrderHistories(): List<OrderHistory> {
        val rawJson = preferences.getString(KEY_ORDER_HISTORY, null) ?: return emptyList()
        return runCatching {
            val jsonArray = JSONArray(rawJson)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val orderObject = jsonArray.optJSONObject(index) ?: continue
                    add(
                        OrderHistory(
                            id = orderObject.optLong(KEY_ID),
                            items = decodeCartItems(orderObject.optJSONArray(KEY_ITEMS) ?: JSONArray()),
                            totalPrice = orderObject.optDouble(KEY_TOTAL_PRICE),
                            timestamp = orderObject.optLong(KEY_TIMESTAMP)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveOrderHistories(orderHistories: List<OrderHistory>) {
        val jsonArray = JSONArray()
        orderHistories.forEach { orderHistory ->
            jsonArray.put(
                JSONObject().apply {
                    put(KEY_ID, orderHistory.id)
                    put(KEY_TOTAL_PRICE, orderHistory.totalPrice)
                    put(KEY_TIMESTAMP, orderHistory.timestamp)
                    put(KEY_ITEMS, encodeCartItems(orderHistory.items))
                }
            )
        }
        preferences.edit().putString(KEY_ORDER_HISTORY, jsonArray.toString()).apply()
    }

    private fun encodeCartItems(cartItems: List<CartItem>): JSONArray {
        val jsonArray = JSONArray()
        cartItems.forEach { cartItem ->
            jsonArray.put(
                JSONObject().apply {
                    put(KEY_QUANTITY, cartItem.quantity)
                    put(
                        KEY_FOOD_ITEM,
                        JSONObject().apply {
                            put(KEY_ID, cartItem.foodItem.id)
                            put(KEY_NAME, cartItem.foodItem.name)
                            put(KEY_PRICE, cartItem.foodItem.price)
                            put(KEY_IMAGE_RES, cartItem.foodItem.imageRes)
                            put(KEY_CATEGORY, cartItem.foodItem.category)
                        }
                    )
                }
            )
        }
        return jsonArray
    }

    private fun decodeCartItems(jsonArray: JSONArray): List<CartItem> {
        return buildList {
            for (index in 0 until jsonArray.length()) {
                val cartItemObject = jsonArray.optJSONObject(index) ?: continue
                val foodItemObject = cartItemObject.optJSONObject(KEY_FOOD_ITEM) ?: continue
                add(
                    CartItem(
                        foodItem = FoodItem(
                            id = foodItemObject.optInt(KEY_ID),
                            name = foodItemObject.optString(KEY_NAME),
                            price = foodItemObject.optString(KEY_PRICE),
                            imageRes = foodItemObject.optInt(KEY_IMAGE_RES),
                            category = foodItemObject.optString(KEY_CATEGORY)
                        ),
                        quantity = cartItemObject.optInt(KEY_QUANTITY)
                    )
                )
            }
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "order_history_preferences"
        private const val KEY_ORDER_HISTORY = "order_history"
        private const val KEY_ID = "id"
        private const val KEY_ITEMS = "items"
        private const val KEY_TOTAL_PRICE = "total_price"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_QUANTITY = "quantity"
        private const val KEY_FOOD_ITEM = "food_item"
        private const val KEY_NAME = "name"
        private const val KEY_PRICE = "price"
        private const val KEY_IMAGE_RES = "image_res"
        private const val KEY_CATEGORY = "category"
    }
}

package com.example.snacksquad.data.repository

import com.example.snacksquad.R
import com.example.snacksquad.data.model.FoodItem

class FoodRepository {
    fun getFoodItems(): List<FoodItem> {
        return listOf(
            FoodItem(
                id = 1,
                name = "Lays",
                price = "\$3",
                imageRes = R.drawable.lays,
                category = "Chips"
            ),
            FoodItem(
                id = 2,
                name = "Burger",
                price = "\$7",
                imageRes = R.drawable.burger,
                category = "Fast Food"
            ),
            FoodItem(
                id = 3,
                name = "Pizza",
                price = "\$8",
                imageRes = R.drawable.pizza,
                category = "Fast Food"
            ),
            FoodItem(
                id = 4,
                name = "Cheese Bread",
                price = "\$10",
                imageRes = R.drawable.bread,
                category = "Bakery"
            ),
            FoodItem(
                id = 5,
                name = "Cakes",
                price = "\$10",
                imageRes = R.drawable.cakes,
                category = "Dessert"
            ),
            FoodItem(
                id = 6,
                name = "Chocolates",
                price = "\$10",
                imageRes = R.drawable.chocolates,
                category = "Dessert"
            )
        )
    }
}

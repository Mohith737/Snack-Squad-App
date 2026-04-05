package com.example.snacksquad.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.snacksquad.data.model.CartItem
import com.example.snacksquad.databinding.CartItemBinding
import com.example.snacksquad.util.ImageUtils

class CartAdapter(
    private val onRemoveItem: (Int) -> Unit,
    private val onIncreaseQuantity: (Int) -> Unit,
    private val onDecreaseQuantity: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).foodItem.name.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.apply {
                cartFoodName.text = item.foodItem.name
                cartItemPrice.text = item.foodItem.price
                ImageUtils.loadOptimizedImage(root.context, item.foodItem.imageRes, cartImage)
                cartItemQuantity.text = item.quantity.toString()

                minusButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        onDecreaseQuantity(position)
                    }
                }
                addButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        onIncreaseQuantity(position)
                    }
                }

                deleteButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        onRemoveItem(position)
                    }
                }
            }
        }
    }

    private class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.foodItem.id == newItem.foodItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}

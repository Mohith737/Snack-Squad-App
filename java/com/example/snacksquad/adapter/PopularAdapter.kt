package com.example.snacksquad.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.snacksquad.data.model.FoodItem
import com.example.snacksquad.databinding.PopularItemBinding
import com.example.snacksquad.util.ImageUtils

class PopularAdapter(
    private val onAddToCartClick: (FoodItem) -> Unit
) : ListAdapter<FoodItem, PopularAdapter.PopularViewHolder>(FoodItemDiffCallback()) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).name.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(
            PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onAddToCartClick
        )
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PopularViewHolder(
        private val binding: PopularItemBinding,
        private val onAddToCartClick: (FoodItem) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodItem) {
            binding.FoodNamePopular.text = item.name
            binding.PricePopular.text = item.price
            ImageUtils.loadOptimizedImage(binding.root.context, item.imageRes, binding.ImagePopular)
            binding.AddtoCartPopular.setOnClickListener {
                onAddToCartClick(item)
            }
        }
    }

    private class FoodItemDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }
}

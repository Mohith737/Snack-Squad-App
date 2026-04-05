package com.example.snacksquad.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snacksquad.R
import com.example.snacksquad.data.model.OrderHistory
import com.example.snacksquad.databinding.HistoryItemBinding
import java.util.Date

class HistoryAdapter :
    ListAdapter<OrderHistory, HistoryAdapter.HistoryViewHolder>(OrderHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(
        private val binding: HistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderHistory: OrderHistory) {
            val context = binding.root.context
            val totalItems = orderHistory.items.sumOf { it.quantity }
            val formattedDate = DateFormat.format(DATE_PATTERN, Date(orderHistory.timestamp)).toString()

            binding.historyDate.text = context.getString(R.string.history_date_format, formattedDate)
            binding.historyItemCount.text =
                context.getString(R.string.history_item_count_format, totalItems)
            binding.historyTotalPrice.text =
                context.getString(R.string.history_total_price_format, orderHistory.totalPrice)
        }
    }

    private class OrderHistoryDiffCallback : DiffUtil.ItemCallback<OrderHistory>() {
        override fun areItemsTheSame(oldItem: OrderHistory, newItem: OrderHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderHistory, newItem: OrderHistory): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val DATE_PATTERN = "dd MMM yyyy, hh:mm a"
    }
}

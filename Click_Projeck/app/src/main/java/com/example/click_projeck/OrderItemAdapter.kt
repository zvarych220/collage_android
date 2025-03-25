package com.example.click_projeck

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.click_projeck.databinding.ItemOrderProductBinding
import data.OrderItem
import data.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import data.AppDatabase

class OrderItemAdapter : ListAdapter<OrderItem, OrderItemAdapter.OrderItemViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderItemViewHolder(private val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItem: OrderItem) {
            GlobalScope.launch(Dispatchers.Main) {
                val db = AppDatabase.getInstance(binding.root.context)
                val product = db.productDao().getProductById(orderItem.productId)

                product?.let {
                    binding.productName.text = it.name
                    binding.productQuantity.text = "Quantity: ${orderItem.quantity}"
                    binding.productPrice.text = "Price: ${orderItem.pricePerItem} x ${orderItem.quantity} = ${orderItem.pricePerItem * orderItem.quantity}"
                }
            }
        }
    }
}

class OrderItemDiffCallback : DiffUtil.ItemCallback<OrderItem>() {
    override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
        return oldItem == newItem
    }
}
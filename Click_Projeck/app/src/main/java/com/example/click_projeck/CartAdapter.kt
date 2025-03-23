package com.example.click_projeck

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import data.CartItem
import data.Product

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit
) : ListAdapter<CartFragment.CartItemWithDetails, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItemWithDetails = getItem(position)
        holder.bind(cartItemWithDetails)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage) // Додано ImageView
        private val btnIncrease: ImageView = itemView.findViewById(R.id.btnIncrease)
        private val btnDecrease: ImageView = itemView.findViewById(R.id.btnDecrease)
        private val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)

        fun bind(cartItemWithDetails: CartFragment.CartItemWithDetails) {
            val cartItem = cartItemWithDetails.cartItem
            val product = cartItemWithDetails.product

            // Встановлення даних
            tvProductName.text = product?.name ?: "Товар ID: ${cartItem.productId}"
            tvQuantity.text = cartItem.quantity.toString()

            // Встановлення зображення товару
            product?.let {
                try {
                    if (it.imageUrl.isNotEmpty()) {
                        val imageBytes = Base64.decode(it.imageUrl, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        ivProductImage.setImageBitmap(bitmap)
                    } else {
                        ivProductImage.setImageResource(R.drawable.placeholder_image)
                    }
                } catch (e: Exception) {
                    ivProductImage.setImageResource(R.drawable.placeholder_image)
                }
            } ?: run {
                ivProductImage.setImageResource(R.drawable.placeholder_image)
            }

            // Обробники кліків
            btnIncrease.setOnClickListener {
                onQuantityChanged(cartItem, cartItem.quantity + 1)
            }

            btnDecrease.setOnClickListener {
                if (cartItem.quantity > 1) {
                    onQuantityChanged(cartItem, cartItem.quantity - 1)
                } else {
                    onItemRemoved(cartItem)
                }
            }

            btnRemove.setOnClickListener {
                onItemRemoved(cartItem)
            }
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartFragment.CartItemWithDetails>() {
        override fun areItemsTheSame(
            oldItem: CartFragment.CartItemWithDetails,
            newItem: CartFragment.CartItemWithDetails
        ): Boolean {
            return oldItem.cartItem.id == newItem.cartItem.id
        }

        override fun areContentsTheSame(
            oldItem: CartFragment.CartItemWithDetails,
            newItem: CartFragment.CartItemWithDetails
        ): Boolean {
            return oldItem == newItem
        }
    }
}
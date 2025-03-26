package com.example.click_projeck

import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.click_projeck.databinding.ItemProduct1Binding
import data.Product

class ProductAdapter1 : ListAdapter<Product, ProductAdapter1.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProduct1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(private val binding: ItemProduct1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = "₴${product.price}"
            binding.textViewProductDescription.text = product.description ?: ""

            try {
                if (product.imageUrl.isNotEmpty()) {
                    val imageBytes = Base64.decode(product.imageUrl, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.imageViewProduct.setImageBitmap(bitmap)
                } else {
                    binding.imageViewProduct.setImageResource(R.drawable.placeholder_image)
                }
            } catch (e: Exception) {
                binding.imageViewProduct.setImageResource(R.drawable.placeholder_image)
            }

            binding.root.setOnClickListener {
                product.id?.let { productId ->
                    val bundle = Bundle().apply {
                        putInt("productId", productId)
                    }
                    binding.root.findNavController().navigate(
                        R.id.action_productListFragment_to_editProductFragment,
                        bundle
                    )
                }
            }

        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
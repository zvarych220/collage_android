package com.example.click_projeck

import android.graphics.BitmapFactory
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

            // Опціонально, якщо ви використовуєте опис
            binding.textViewProductDescription.text = product.description ?: ""

            // Обробка зображення
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

            // Обробка кліків
            binding.root.setOnClickListener {
                product.id?.let { productId ->
                    when (binding.root.findNavController().currentDestination?.id) {
                        R.id.productFragment -> binding.root.findNavController().navigate(
                            ProductFragmentDirections.actionProductFragmentToProductDetailFragment(productId)
                        )
                        R.id.productListFragment -> binding.root.findNavController().navigate(
                            ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(productId)
                        )
                    }
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
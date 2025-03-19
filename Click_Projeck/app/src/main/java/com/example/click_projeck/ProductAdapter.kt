package com.example.click_projeck

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.click_projeck.databinding.ItemProductBinding
import data.Product

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = "₴${product.price}"

            // Convert Base64 string to Bitmap and display
            try {
                if (product.imageUrl.isNotEmpty()) {
                    val imageBytes = Base64.decode(product.imageUrl, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.imageViewProduct.setImageBitmap(bitmap)
                } else {
                    // Use placeholder if no image URL is provided
                    binding.imageViewProduct.setImageResource(R.drawable.placeholder_image)
                }
            } catch (e: Exception) {
                // In case of error (invalid Base64 string, etc), use placeholder
                binding.imageViewProduct.setImageResource(R.drawable.placeholder_image)
            }

            // Set click listener to navigate to product detail
            binding.root.setOnClickListener {
                product.id?.let { productId ->
                    // Navigate from ProductFragment to ProductDetailFragment
                    if (binding.root.findNavController().currentDestination?.id == R.id.productFragment) {
                        binding.root.findNavController().navigate(
                            ProductFragmentDirections.actionProductFragmentToProductDetailFragment(productId)
                        )
                    }
                    // Navigate from ProductListFragment to ProductDetailFragment
                    else if (binding.root.findNavController().currentDestination?.id == R.id.productListFragment) {
                        binding.root.findNavController().navigate(
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
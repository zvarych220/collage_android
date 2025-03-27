package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.click_projeck.databinding.FragmentProductBinding
import data.AppDatabase
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.recyclerViewProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val database = AppDatabase.getInstance(requireContext())
                val products = database.productDao().getAllProducts()

                if (products.isNotEmpty()) {
                    binding.textViewNoProducts.visibility = View.GONE
                    productAdapter.submitList(products)
                } else {
                    binding.textViewNoProducts.visibility = View.VISIBLE
                    binding.textViewNoProducts.text = "No products available"
                }
            } catch (e: Exception) {
                binding.textViewNoProducts.text = "Error loading products: ${e.message}"
                binding.textViewNoProducts.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
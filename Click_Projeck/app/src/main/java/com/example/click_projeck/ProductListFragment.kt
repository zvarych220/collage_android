package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.click_projeck.databinding.FragmentProductListBinding
import data.AppDatabase
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val productDao = AppDatabase.getInstance(requireContext()).productDao()
                val products = productDao.getAllProducts()

                if (products.isEmpty()) {
                    Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show()
                } else {
                    productAdapter.submitList(products)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading products: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
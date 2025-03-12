package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentCreateProductBinding
import data.AppDatabase
import data.Product
import kotlinx.coroutines.launch

class CreateProductFragment : Fragment() {
    private var _binding: FragmentCreateProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val priceStr = binding.etProductPrice.text.toString().trim()
        val imageUrl = binding.etProductImageUrl.text.toString().trim()
        val category = binding.etProductCategory.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = try {
            priceStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Invalid price format", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveProduct.isEnabled = false

        val product = Product(
            name = name,
            description = description,
            price = price,
            imageUrl = imageUrl,
            category = category
        )

        lifecycleScope.launch {
            try {
                val productDao = AppDatabase.getInstance(requireContext()).productDao()
                val id = productDao.insertProduct(product)

                if (id > 0) {
                    Toast.makeText(requireContext(), "Product saved successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Failed to save product", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSaveProduct.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
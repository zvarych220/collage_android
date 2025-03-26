package com.example.click_projeck

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.click_projeck.databinding.FragmentEditProductBinding
import data.AppDatabase
import data.Product
import kotlinx.coroutines.launch

class EditProductFragment : Fragment() {
    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentProduct: Product

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getInt("productId") ?: -1
        if (productId == -1) {
            Toast.makeText(requireContext(), "Invalid product", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        loadProductData(productId)
        setupClickListeners()
    }

    private fun loadProductData(productId: Int) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val productDao = AppDatabase.getInstance(requireContext()).productDao()
                val product = productDao.getProductById(productId)

                if (product != null) {
                    currentProduct = product
                    displayProductData(product)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Product not found",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading product: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayProductData(product: Product) {
        // Display product image
        try {
            if (product.imageUrl.isNotEmpty()) {
                val imageBytes = Base64.decode(product.imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.productImageView.setImageBitmap(bitmap)
            } else {
                binding.productImageView.setImageResource(R.drawable.placeholder_image)
            }
        } catch (e: Exception) {
            binding.productImageView.setImageResource(R.drawable.placeholder_image)
        }

        // Set text fields with current values
        binding.nameEditText.setText(product.name)
        binding.descriptionEditText.setText(product.description)
        binding.priceEditText.setText(product.price.toString())
        binding.categoryEditText.setText(product.category)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveButton.setOnClickListener {
            if (validateInput()) {
                updateProduct()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (binding.nameEditText.text.isNullOrBlank()) {
            binding.nameInputLayout.error = "Name is required"
            isValid = false
        } else {
            binding.nameInputLayout.error = null
        }

        if (binding.priceEditText.text.isNullOrBlank()) {
            binding.priceInputLayout.error = "Price is required"
            isValid = false
        } else {
            try {
                binding.priceEditText.text.toString().toDouble()
                binding.priceInputLayout.error = null
            } catch (e: NumberFormatException) {
                binding.priceInputLayout.error = "Invalid price"
                isValid = false
            }
        }

        return isValid
    }

    private fun updateProduct() {
        binding.progressBar.visibility = View.VISIBLE

        val updatedProduct = currentProduct.copy(
            name = binding.nameEditText.text.toString(),
            description = binding.descriptionEditText.text.toString(),
            price = binding.priceEditText.text.toString().toDouble(),
            category = binding.categoryEditText.text.toString()
        )

        lifecycleScope.launch {
            try {
                val productDao = AppDatabase.getInstance(requireContext()).productDao()
                productDao.insertProduct(updatedProduct)

                Toast.makeText(
                    requireContext(),
                    "Product updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error updating product: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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
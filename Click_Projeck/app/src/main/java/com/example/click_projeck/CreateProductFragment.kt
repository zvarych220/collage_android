package com.example.click_projeck
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentCreateProductBinding
import data.AppDatabase
import data.Product
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CreateProductFragment : Fragment() {
    private var _binding: FragmentCreateProductBinding? = null
    private val binding get() = _binding!!

    private var productImageBitmap: Bitmap? = null

    // Реєстрація для результатів активності камери
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val image = result.data?.extras?.get("data") as? Bitmap
            image?.let {
                binding.productImage.setImageBitmap(it)
                binding.productImage.visibility = View.VISIBLE
                productImageBitmap = it

                // Конвертуємо зображення в Base64 для збереження
                val imageBase64 = convertBitmapToBase64(it)
                binding.etProductImageUrl.setText(imageBase64)
            }
        }
    }

    // Реєстрація для результатів активності галереї
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        it
                    )
                    binding.productImage.setImageBitmap(bitmap)
                    binding.productImage.visibility = View.VISIBLE
                    productImageBitmap = bitmap

                    // Конвертуємо зображення в Base64 для збереження
                    val imageBase64 = convertBitmapToBase64(bitmap)
                    binding.etProductImageUrl.setText(imageBase64)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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

        // Налаштування обробників подій для кнопок
        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Додаємо обробники для кнопок камери та галереї
        binding.cameraButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }

        binding.galleryButton.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            galleryLauncher.launch(galleryIntent)
        }
    }

    // Функція для конвертації Bitmap в Base64
    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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
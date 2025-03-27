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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.click_projeck.databinding.FragmentCreateProductBinding
import data.AppDatabase
import data.Product
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CreateProductFragment : Fragment() {
    private var _binding: FragmentCreateProductBinding? = null
    private val binding get() = _binding!!

    // Список для зберігання фотографій продукту
    private val productImages = mutableListOf<ProductImage>()
    private lateinit var imageAdapter: ProductImageAdapter

    // Реєстрація для результатів активності камери
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val image = result.data?.extras?.get("data") as? Bitmap
            image?.let {
                addProductImage(it)
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
                    addProductImage(bitmap)
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

        // Ініціалізація адаптера та ViewPager2
        initImageViewPager()

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

    private fun initImageViewPager() {
        imageAdapter = ProductImageAdapter(
            productImages,
            onDeleteClick = { position ->
                deleteProductImage(position)
            },
            onMainImageClick = { position ->
                setMainImage(position)
            }
        )

        binding.imageViewPager.apply {
            adapter = imageAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateDotsIndicator(position)
                }
            })
        }
    }

    private fun addProductImage(bitmap: Bitmap) {
        val base64 = convertBitmapToBase64(bitmap)
        // Якщо це перше зображення, робимо його головним
        val isMain = productImages.isEmpty()

        productImages.add(ProductImage(bitmap, base64, isMain))

        // Оновлюємо UI
        updateImagesUI()

        // Оновлюємо поле для збереження URL головного зображення
        updateMainImageUrl()
    }

    private fun deleteProductImage(position: Int) {
        val wasMain = productImages[position].isMain
        productImages.removeAt(position)

        // Якщо видалене зображення було головним і є інші зображення,
        // встановлюємо інше як головне
        if (wasMain && productImages.isNotEmpty()) {
            productImages[0].isMain = true
        }

        // Оновлюємо UI
        updateImagesUI()

        // Оновлюємо поле для збереження URL головного зображення
        updateMainImageUrl()
    }

    private fun setMainImage(position: Int) {
        productImages.forEach { it.isMain = false }
        productImages[position].isMain = true

        // Оновлення URL головного фото
        binding.etProductImageUrl.setText(productImages[position].base64)

        // Оновлення адаптера
        imageAdapter.notifyDataSetChanged()
    }

    private fun updateImagesUI() {
        if (productImages.isEmpty()) {
            binding.imageViewPager.visibility = View.GONE
            binding.dotsIndicator.visibility = View.GONE
            binding.tvNoImages.visibility = View.VISIBLE
        } else {
            binding.imageViewPager.visibility = View.VISIBLE
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.tvNoImages.visibility = View.GONE

            // Оновлюємо адаптер
            imageAdapter.notifyDataSetChanged()

            // Оновлюємо індикатор точок
            createDotsIndicator()
            updateDotsIndicator(binding.imageViewPager.currentItem)
        }
    }

    private fun createDotsIndicator() {
        binding.dotsIndicator.removeAllViews()

        for (i in productImages.indices) {
            val dot = ImageView(context)
            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.dot_indicator_inactive
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params

            binding.dotsIndicator.addView(dot)
        }
    }

    private fun updateDotsIndicator(position: Int) {
        for (i in 0 until binding.dotsIndicator.childCount) {
            val dot = binding.dotsIndicator.getChildAt(i) as ImageView
            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == position) R.drawable.dot_indicator_active else R.drawable.dot_indicator_inactive
                )
            )
        }
    }

    private fun updateMainImageUrl() {
        val mainImage = productImages.find { it.isMain }
        binding.etProductImageUrl.setText(mainImage?.base64 ?: "")
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

        if (productImages.isEmpty()) {
            Toast.makeText(requireContext(), "Please add at least one image", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = try {
            priceStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Invalid price format", Toast.LENGTH_SHORT).show()
            return
        }

        // Перетворення списку фото в JSON
        val allImagesJson = productImages.joinToString(",", "[", "]") {
            """{"base64":"${it.base64}","isMain":${it.isMain}}"""
        }

        val product = Product(
            name = name,
            description = description,
            price = price,
            imageUrl = imageUrl, // Головне фото
            category = category,
            allImages = allImagesJson // Усі фото
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
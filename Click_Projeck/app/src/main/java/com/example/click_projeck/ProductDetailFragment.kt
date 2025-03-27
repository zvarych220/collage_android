package com.example.click_projeck

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.click_projeck.databinding.FragmentProductDetailBinding
import data.AppDatabase
import data.CartItem
import kotlinx.coroutines.launch
import org.json.JSONArray

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()
    private val imageList = mutableListOf<Bitmap>()
    private lateinit var imageAdapter: ProductImageViewPagerAdapter
    private val indicators = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupViewPager()
        loadProductDetails()

        // Додаємо обробник натискання на кнопку "Додати до кошика"
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun setupViewPager() {
        imageAdapter = ProductImageViewPagerAdapter(imageList)
        binding.viewPagerImages.adapter = imageAdapter

        binding.viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
    }

    private fun loadProductDetails() {
        lifecycleScope.launch {
            try {
                val productDao = AppDatabase.getInstance(requireContext()).productDao()
                val product = productDao.getProductById(args.productId)

                product?.let {
                    binding.tvProductTitle.text = it.name
                    binding.tvPrice.text = "₴${it.price}"
                    binding.tvCategory.text = it.category
                    binding.tvDescription.text = it.description

                    loadAllImages(it.allImages, it.imageUrl)
                }
            } catch (e: Exception) {
                Log.e("ProductDetail", "Error loading product details: ${e.message}")
            }
        }
    }

    private fun loadAllImages(allImagesJson: String, mainImageUrl: String) {
        try {
            imageList.clear()

            if (allImagesJson.isNotEmpty()) {
                try {
                    val jsonArray = JSONArray(allImagesJson)

                    for (i in 0 until jsonArray.length()) {
                        val imageBase64 = jsonArray.getString(i)
                        val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        if (bitmap != null) {
                            imageList.add(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProductDetail", "Error parsing JSON: ${e.message}")
                    if (mainImageUrl.isNotEmpty()) {
                        val mainImageBytes = Base64.decode(mainImageUrl, Base64.DEFAULT)
                        val mainBitmap = BitmapFactory.decodeByteArray(mainImageBytes, 0, mainImageBytes.size)
                        if (mainBitmap != null) {
                            imageList.add(mainBitmap)
                        }
                    }
                }
            }

            if (imageList.isEmpty() && mainImageUrl.isNotEmpty()) {
                val mainImageBytes = Base64.decode(mainImageUrl, Base64.DEFAULT)
                val mainBitmap = BitmapFactory.decodeByteArray(mainImageBytes, 0, mainImageBytes.size)
                if (mainBitmap != null) {
                    imageList.add(mainBitmap)
                }
            }

            if (imageList.isEmpty()) {
                val placeholder = BitmapFactory.decodeResource(resources, R.drawable.placeholder_image)
                imageList.add(placeholder)
            }

            imageAdapter.notifyDataSetChanged()
            setupIndicators()

        } catch (e: Exception) {
            Log.e("ProductDetail", "Error loading images: ${e.message}")
            imageList.clear()
            val placeholder = BitmapFactory.decodeResource(resources, R.drawable.placeholder_image)
            imageList.add(placeholder)
            imageAdapter.notifyDataSetChanged()
            setupIndicators()
        }
    }

    private fun setupIndicators() {
        // Clear existing indicators
        indicators.clear()
        binding.layoutIndicators.removeAllViews()

        if (imageList.size <= 1) return

        for (i in imageList.indices) {
            val indicator = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_dot_indicator, binding.layoutIndicators, false) as ImageView

            if (i == 0) {
                indicator.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active))
            } else {
                indicator.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive))
            }

            // Add to layout
            binding.layoutIndicators.addView(indicator)
            indicators.add(indicator)
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in indicators.indices) {
            if (i == position) {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active))
            } else {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive))
            }
        }
    }

    private fun addToCart() {
        lifecycleScope.launch {
            try {
                val sessionManager = SessionManager(requireContext())
                val userEmail = sessionManager.getUserDetails()[SessionManager.KEY_USER_EMAIL]
                if (userEmail == null) {
                    Toast.makeText(requireContext(), "Користувач не авторизований", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val userDao = AppDatabase.getInstance(requireContext()).userDao()
                val user = userDao.getUserByEmail(userEmail)
                if (user == null) {
                    Toast.makeText(requireContext(), "Користувач не знайдений", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val productDao = AppDatabase.getInstance(requireContext()).productDao()
                val product = productDao.getProductById(args.productId)
                if (product == null) {
                    Toast.makeText(requireContext(), "Товар не знайдений", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val cartDao = AppDatabase.getInstance(requireContext()).cartDao()
                val existingCartItem = cartDao.getCartItemByUserAndProduct(user.id!!, product.id!!)
                if (existingCartItem != null) {
                    // Якщо товар вже є в кошику, збільшуємо кількість
                    cartDao.updateQuantity(existingCartItem.id, existingCartItem.quantity + 1)
                } else {
                    // Якщо товару немає в кошику, додаємо новий запис
                    val cartItem = CartItem(
                        userId = user.id!!,
                        productId = product.id!!,
                        quantity = 1
                    )
                    cartDao.insert(cartItem)
                }

                Toast.makeText(requireContext(), "Товар додано до кошика", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ProductDetail", "Error adding to cart: ${e.message}")
                Toast.makeText(requireContext(), "Помилка при додаванні до кошика", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
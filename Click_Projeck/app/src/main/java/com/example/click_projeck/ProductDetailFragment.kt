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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.click_projeck.databinding.FragmentProductDetailBinding
import data.AppDatabase
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

        // Set up back button
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Setup ViewPager
        setupViewPager()

        // Load product details
        loadProductDetails()
    }

    private fun setupViewPager() {
        imageAdapter = ProductImageViewPagerAdapter(imageList)
        binding.viewPagerImages.adapter = imageAdapter

        // Add page change listener to update indicators
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
                    // Set product information
                    binding.tvProductTitle.text = it.name
                    binding.tvPrice.text = "₴${it.price}"
                    binding.tvCategory.text = it.category
                    binding.tvDescription.text = it.description

                    // Load all images
                    loadAllImages(it.allImages, it.imageUrl)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadAllImages(allImagesJson: String, mainImageUrl: String) {
        try {
            // Clear existing images
            imageList.clear()

            // Check if allImagesJson is not empty
            if (allImagesJson.isNotEmpty()) {
                try {
                    // Parse JSON string with all images
                    val jsonArray = JSONArray(allImagesJson)

                    // Add all images from the JSON array
                    for (i in 0 until jsonArray.length()) {
                        val imageBase64 = jsonArray.getString(i)
                        val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        if (bitmap != null) {
                            imageList.add(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    // Log the specific JSON parsing error
                    Log.e("ProductDetail", "Error parsing JSON: ${e.message}")
                    // If JSON parsing fails, try to use the main image
                    if (mainImageUrl.isNotEmpty()) {
                        val mainImageBytes = Base64.decode(mainImageUrl, Base64.DEFAULT)
                        val mainBitmap = BitmapFactory.decodeByteArray(mainImageBytes, 0, mainImageBytes.size)
                        if (mainBitmap != null) {
                            imageList.add(mainBitmap)
                        }
                    }
                }
            }

            // If no images were added from JSON, check if there's a main image
            if (imageList.isEmpty() && mainImageUrl.isNotEmpty()) {
                val mainImageBytes = Base64.decode(mainImageUrl, Base64.DEFAULT)
                val mainBitmap = BitmapFactory.decodeByteArray(mainImageBytes, 0, mainImageBytes.size)
                if (mainBitmap != null) {
                    imageList.add(mainBitmap)
                }
            }

            // If still no images, add placeholder
            if (imageList.isEmpty()) {
                val placeholder = BitmapFactory.decodeResource(resources, R.drawable.placeholder_image)
                imageList.add(placeholder)
            }

            // Update adapter and setup indicators
            imageAdapter.notifyDataSetChanged()
            setupIndicators()

        } catch (e: Exception) {
            // Log the general error
            Log.e("ProductDetail", "Error loading images: ${e.message}")
            // Handle error - add placeholder image
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

        // If there's only one image, don't show indicators
        if (imageList.size <= 1) return

        // Add indicators for each image
        for (i in imageList.indices) {
            val indicator = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_dot_indicator, binding.layoutIndicators, false) as ImageView

            // Set initial state
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
        // Update all indicators' state
        for (i in indicators.indices) {
            if (i == position) {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active))
            } else {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
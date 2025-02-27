package com.example.click_projeck

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.click_projeck.databinding.FragmentCameraBinding
import java.io.ByteArrayOutputStream

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadImage()

        val cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val image = result.data?.extras?.get("data") as Bitmap
                binding.imageView.setImageBitmap(image)
                saveImage(image)
            }
        }

        val galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        selectedImageUri
                    )
                    binding.imageView.setImageBitmap(bitmap)
                    saveImage(bitmap)
                }
            }
        }

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

    private fun saveImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

        requireActivity().getSharedPreferences("CameraApp", Context.MODE_PRIVATE)
            .edit()
            .putString("cameraImage", encodedImage) // Використовуємо новий ключ
            .apply()
    }


    private fun loadImage() {
        val encodedImage = requireActivity().getSharedPreferences("CameraApp", Context.MODE_PRIVATE)
            .getString("cameraImage", null) // Використовуємо новий ключ
        if (encodedImage != null) {
            val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            binding.imageView.setImageBitmap(bitmap)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
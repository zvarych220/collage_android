package com.example.click_projeck

import android.app.Activity
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.click_projeck.databinding.EditProfBinding
import java.io.ByteArrayOutputStream

class EditProfile : Fragment() {
    private var _binding: EditProfBinding? = null
    private val binding get() = _binding!!

    // Змінна для збереження нового фото профілю (якщо його змінили)
    private var newProfileBitmap: Bitmap? = null

    // Лончер для камери
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val image = result.data?.extras?.get("data") as? Bitmap
            image?.let {
                binding.profileImage.setImageBitmap(it)
                // Зберігаємо фото у змінну, але не в SharedPreferences,
                // поки користувач не натисне кнопку Save
                newProfileBitmap = it
            }
        }
    }

    // Лончер для галереї
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    it
                )
                binding.profileImage.setImageBitmap(bitmap)
                newProfileBitmap = bitmap
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EditProfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Завантаження існуючих даних користувача
        loadUserData()
        loadProfileImage()

        // Обробка натискання на кнопку для зйомки фото
        binding.cameraButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }

        // Обробка натискання на кнопку для вибору фото з галереї
        binding.galleryButton.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            galleryLauncher.launch(galleryIntent)
        }

        // Обробка натискання кнопки Save: зберігаємо всі зміни одночасно
        binding.button3.setOnClickListener {
            saveUserData()
        }
    }


    private fun loadUserData() {
        val prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val currentEmail = prefs.getString("CurrentUser", "") ?: ""
        if (currentEmail.isNotEmpty()) {
            val email = prefs.getString("${currentEmail}_email", "") ?: ""
            val nickname = prefs.getString("${currentEmail}_nickname", "") ?: ""
            val password = prefs.getString("${currentEmail}_password", "") ?: ""

            binding.editTextText.setText(nickname)
            binding.editTextText2.setText(email)
            binding.editTextText3.setText(password)
        }
    }


    private fun loadProfileImage() {
        val prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val encodedImage = prefs.getString("profileImage", null)
        if (encodedImage != null) {
            val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            binding.profileImage.setImageBitmap(bitmap)
        }
    }


    private fun saveUserData() {
        val prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val currentEmail = prefs.getString("CurrentUser", "") ?: ""

        if (currentEmail.isEmpty()) {
            Toast.makeText(context, "No current user", Toast.LENGTH_SHORT).show()
            return
        }

        // Зчитуємо нові дані з полів введення
        val newNickname = binding.editTextText.text.toString()
        val newEmail = binding.editTextText2.text.toString()
        val newPassword = binding.editTextText3.text.toString()

        if (newNickname.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Якщо email змінено, видаляємо старі дані та оновлюємо "CurrentUser"
        if (newEmail != currentEmail) {
            prefs.edit()
                .remove("${currentEmail}_email")
                .remove("${currentEmail}_nickname")
                .remove("${currentEmail}_password")
                .putString("CurrentUser", newEmail)
                .apply()
        }

        // Зберігаємо оновлені дані користувача у SharedPreferences "UserData"
        prefs.edit()
            .putString("${newEmail}_email", newEmail)
            .putString("${newEmail}_nickname", newNickname)
            .putString("${newEmail}_password", newPassword)
            .apply()

        // Якщо користувач обрав нове фото, зберігаємо його у SharedPreferences "UserProfile"
        newProfileBitmap?.let {
            saveProfileImage(it)
        }

        Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show()
    }


    private fun saveProfileImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            .edit()
            .putString("profileImage", encodedImage)
            .apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

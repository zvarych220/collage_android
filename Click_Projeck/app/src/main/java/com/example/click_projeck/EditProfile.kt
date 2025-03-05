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
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.EditProfBinding
import data.AppDatabase
import data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class EditProfile : Fragment() {
    private var _binding: EditProfBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    private var newProfileBitmap: Bitmap? = null
    private var currentUserEmail: String = ""

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val image = result.data?.extras?.get("data") as? Bitmap
            image?.let {
                binding.profileImage.setImageBitmap(it)
                newProfileBitmap = it
            }
        }
    }

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
        savedInstanceState: Bundle?
    ): View {
        _binding = EditProfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ініціалізація бази даних
        db = AppDatabase.getInstance(requireContext())

        // Отримання поточного email
        val prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        currentUserEmail = prefs.getString("CurrentUser", "") ?: ""

        loadUserData()

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

        binding.button3.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        if (currentUserEmail.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val user = db.userDao().getUserByEmail(currentUserEmail)

                withContext(Dispatchers.Main) {
                    user?.let {
                        binding.editTextText.setText(it.name)
                        binding.editTextText2.setText(it.email)
                        binding.editTextText3.setText(it.password)

                        // Завантаження зображення профілю
                        it.profileImage?.let { encodedImage ->
                            val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            binding.profileImage.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(
        nickname: String,
        email: String,
        password: String
    ): Boolean {
        return when {
            nickname.isEmpty() -> {
                Toast.makeText(context, "Введіть нікнейм", Toast.LENGTH_SHORT).show()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(context, "Некоректний формат email", Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Toast.makeText(context, "Пароль має бути не менше 6 символів", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun saveUserData() {
        val newNickname = binding.editTextText.text.toString().trim()
        val newEmail = binding.editTextText2.text.toString().trim()
        val newPassword = binding.editTextText3.text.toString().trim()

        // Валідація введених даних
        if (!validateInput(newNickname, newEmail, newPassword)) {
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Пошук поточного користувача
                val currentUser = db.userDao().getUserByEmail(currentUserEmail)

                if (currentUser == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Користувача не знайдено", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Перевірка чи email вже існує (якщо email змінився)
                if (newEmail != currentUserEmail) {
                    val emailExists = db.userDao().isEmailExists(newEmail)
                    if (emailExists > 0) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Користувач з таким email вже існує", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }
                }

                val profileImageString = newProfileBitmap?.let { bitmap ->
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
                }

                // Створення оновленого користувача
                val updatedUser = User(
                    id = currentUser.id,
                    name = newNickname,
                    email = newEmail,
                    password = newPassword,
                    about = currentUser.about,
                    dob = currentUser.dob,
                    profileImage = profileImageString ?: currentUser.profileImage
                )

                // Видалення старого запису, якщо email змінився
                if (currentUserEmail != newEmail) {
                    db.userDao().deleteUserByEmail(currentUserEmail)
                }

                // Оновлення запису в базі даних
                db.userDao().insertUser(updatedUser)

                // Оновлення поточного email в SharedPreferences
                requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
                    .edit()
                    .putString("CurrentUser", newEmail)
                    .apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Дані успішно оновлено", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Повернення до попереднього фрагменту
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Помилка оновлення: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.click_projeck

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentProfileBinding
import data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var sessionManager: SessionManager
    private var isAdmin = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ініціалізація бази даних та SessionManager
        db = AppDatabase.getInstance(requireContext())
        sessionManager = SessionManager(requireContext())

        loadUserData()
        loadUserImage()

        binding.btnGoToEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfile)
        }

        binding.btnAdminPanel.visibility = View.GONE
        binding.btnAdminPanel.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_adminFragment)
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun loadUserData() {
        // Перевіряємо, чи користувач увійшов у систему через sessionManager
        if (sessionManager.isLoggedIn()) {
            val userDetails = sessionManager.getUserDetails()
            val currentUserEmail = userDetails[SessionManager.KEY_USER_EMAIL] ?: ""

            if (currentUserEmail.isNotEmpty()) {
                // Завантаження даних користувача з бази даних
                lifecycleScope.launch(Dispatchers.IO) {
                    val user = db.userDao().getUserByEmail(currentUserEmail)

                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            setText(binding.etEmail, user.email)
                            setText(binding.etNickname, user.name)
                            setText(binding.etAbout, user.about)
                            setText(binding.etDateOfBirth, user.dob)

                            // Перевірка, чи користувач є адміністратором
                            isAdmin = user.isAdmin
                            binding.btnAdminPanel.visibility = if (isAdmin) View.VISIBLE else View.GONE

                            // Перевірка, чи це єдиний користувач, і призначення його адміністратором, якщо потрібно
                            checkAndSetFirstUserAsAdmin(user)
                        } else {
                            setDefaultUserInfo()
                        }
                    }
                }
            } else {
                setDefaultUserInfo()
            }
        } else {
            setDefaultUserInfo()
        }
    }

    private fun checkAndSetFirstUserAsAdmin(user: data.User) {
        if (user.isAdmin) return // Вже адміністратор, нічого робити не потрібно

        lifecycleScope.launch(Dispatchers.IO) {
            val userCount = db.userDao().getUserCount()
            if (userCount == 1 && !user.isAdmin) {
                // Це єдиний користувач, призначити його адміністратором
                user.id?.let {
                    db.userDao().setUserAsAdmin(it)
                    withContext(Dispatchers.Main) {
                        isAdmin = true
                        binding.btnAdminPanel.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setDefaultUserInfo() {
        setText(binding.etEmail, "Email: Not available")
        setText(binding.etNickname, "Nickname: Not available")
        setText(binding.etAbout, "About: Not available")
        setText(binding.etDateOfBirth, "Date of Birth: Not available")
        binding.btnAdminPanel.visibility = View.GONE
    }

    private fun setText(editText: com.google.android.material.textfield.TextInputEditText, text: String) {
        editText.text = Editable.Factory.getInstance().newEditable(text)
    }

    private fun loadUserImage() {
        // Завантаження зображення з SharedPreferences
        val encodedImage = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            .getString("profileImage", null)
        if (encodedImage != null) {
            val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            binding.profileImage.setImageBitmap(bitmap)
        } else {
            // Встановлення зображення за замовчуванням
            binding.profileImage.setImageResource(R.drawable.ic_profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
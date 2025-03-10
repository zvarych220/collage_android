package com.example.click_projeck

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
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

        // Admin button setup - initially hidden
        binding.btnAdminPanel.visibility = View.GONE
        binding.btnAdminPanel.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_adminFragment)
        }

        // кнопку виходу
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
                            binding.tvEmail.text = "Email: ${user.email}"
                            binding.tvNickname.text = "Nickname: ${user.name}"
                            binding.tvAbout.text = "About: ${user.about}"
                            binding.tvDateOfBirth.text = "Date of Birth: ${user.dob}"

                            // Check if user is admin and show the admin button
                            isAdmin = user.isAdmin
                            binding.btnAdminPanel.visibility = if (isAdmin) View.VISIBLE else View.GONE

                            // Check if this is the only user and make them admin if needed
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
        if (user.isAdmin) return // Already admin, nothing to do

        lifecycleScope.launch(Dispatchers.IO) {
            val userCount = db.userDao().getUserCount()
            if (userCount == 1 && !user.isAdmin) {
                // This is the only user, set as admin
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
        binding.tvEmail.text = "Email: Not available"
        binding.tvNickname.text = "Nickname: Not available"
        binding.tvAbout.text = "About: Not available"
        binding.tvDateOfBirth.text = "Date of Birth: Not available"
        binding.btnAdminPanel.visibility = View.GONE
    }

    private fun loadUserImage() {
        // Збереження логіки завантаження зображення з SharedPreferences
        val encodedImage = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            .getString("profileImage", null)
        if (encodedImage != null) {
            val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            binding.profileImage.setImageBitmap(bitmap)
        } else {
            // Встановіть зображення за замовчуванням
            binding.profileImage.setImageResource(R.drawable.ic_profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
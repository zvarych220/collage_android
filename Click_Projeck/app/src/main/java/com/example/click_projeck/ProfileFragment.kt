package com.example.click_projeck

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        loadUserData()
        loadUserImage()

        binding.btnGoToEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfile)
        }
    }

    private fun loadUserData() {
        val prefs = requireContext().getSharedPreferences("UserData", android.content.Context.MODE_PRIVATE)
        val currentUserEmail = prefs.getString("CurrentUser", "") ?: ""


        if (currentUserEmail.isNotEmpty()) {
            val email = prefs.getString("${currentUserEmail}_email", "")
            val nickname = prefs.getString("${currentUserEmail}_nickname", "")

            binding.tvEmail.text = "Email: $email"
            binding.tvNickname.text = "Nickname: $nickname"

        } else {
            binding.tvEmail.text = "Email: Not available"
            binding.tvNickname.text = "Nickname: Not available"
        }
    }

    private fun loadUserImage() {
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
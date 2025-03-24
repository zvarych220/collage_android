package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentUserDetailBinding
import data.AppDatabase
import data.User
import kotlinx.coroutines.launch

class UserDetailFragment : Fragment() {
    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getInt("userId", -1) ?: -1
        if (userId == -1) {
            findNavController().popBackStack()
            return
        }

        loadUser(userId)

        binding.btnDeleteUser.setOnClickListener {
            deleteUser()
        }

        binding.switchAdmin.setOnCheckedChangeListener { _, isChecked ->
            updateUserRole(isChecked)
        }
    }

    private fun loadUser(userId: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val user = db.userDao().getUserById(userId)
            user?.let {
                currentUser = it
                displayUser(it)
            } ?: run {
                findNavController().popBackStack()
            }
        }
    }

    private fun displayUser(user: User) {
        binding.apply {
            tvUserName.text = user.name
            tvUserEmail.text = user.email
            tvUserAbout.text = user.about
            tvUserDob.text = user.dob
            switchAdmin.isChecked = user.isAdmin

            // Load profile image here if available
            // Glide.with(requireContext()).load(user.profileImage).into(ivUserProfile)
        }
    }

    private fun updateUserRole(isAdmin: Boolean) {
        currentUser?.let { user ->
            lifecycleScope.launch {
                val db = AppDatabase.getInstance(requireContext())
                db.userDao().updateUserRole(user.id ?: return@launch, isAdmin)
            }
        }
    }

    private fun deleteUser() {
        currentUser?.let { user ->
            lifecycleScope.launch {
                val db = AppDatabase.getInstance(requireContext())
                db.userDao().deleteUserByEmail(user.email)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
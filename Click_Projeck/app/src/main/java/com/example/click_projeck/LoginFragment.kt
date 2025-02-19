package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val prefs = requireContext().getSharedPreferences("UserData", android.content.Context.MODE_PRIVATE)
                val savedPassword = prefs.getString("${email}_password", null)

                if (savedPassword != null && savedPassword == password) {
                    // Save current user session
                    prefs.edit()
                        .putString("CurrentUser", email)
                        .apply()

                    val nickname = prefs.getString("${email}_nickname", "User")
                    val bundle = Bundle().apply {
                        putString("Name", nickname)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)
                } else {
                    Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveUserData(email: String) {
        requireContext().getSharedPreferences("UserData", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("CurrentUser", email)
            .apply()
    }
}
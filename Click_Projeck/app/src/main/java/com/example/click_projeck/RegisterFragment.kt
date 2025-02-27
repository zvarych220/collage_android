package com.example.click_projeck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val nickname = binding.etNickname.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && nickname.isNotEmpty()) {
                if (password == confirmPassword) {
                    val prefs = requireContext().getSharedPreferences("UserData", android.content.Context.MODE_PRIVATE)
                    if (prefs.getString("${email}_password", null) == null) {
                        // Save user data with proper keys
                        prefs.edit()
                            .putString("${email}_password", password)
                            .putString("${email}_nickname", nickname)
                            .putString("${email}_email", email)
                            .apply()

                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    } else {
                        Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
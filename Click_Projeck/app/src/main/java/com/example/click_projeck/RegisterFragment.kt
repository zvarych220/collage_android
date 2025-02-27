package com.example.click_projeck

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.click_projeck.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch
import java.util.Calendar

import data.AppDatabase
import androidx.lifecycle.lifecycleScope
import data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private val PREFS_NAME = "UserPrefs"
    private val EMAIL_KEY = "email"
    private val PASSWORD_KEY = "password"
    private val USERNAME_KEY = "username"
    private val ABOUT_KEY = "about"
    private val DOB_KEY = "dob"

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPreferences

    // Оголошення змінної БД - розкоментуйте коли додасте необхідні класи
     private lateinit var db: AppDatabase

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

        navController = findNavController()

        // Створення БД - розкоментуйте коли додасте необхідні класи
         db = AppDatabase.getInstance(requireContext())

        // Ініціалізуємо SharedPreferences
        sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Налаштовуємо DatePicker для поля дати народження
        binding.etDateOfBirth.setOnClickListener {
            showDatePicker()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val nickname = binding.etNickname.text.toString().trim()
            val about = binding.etAbout.text.toString().trim()
            val dob = binding.etDateOfBirth.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                nickname.isEmpty() || about.isEmpty() || dob.isEmpty()) {
                Toast.makeText(requireContext(), "Заповніть всі поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Паролі не співпадають", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(null, nickname, email, password, about, dob)
            lifecycleScope.launch(Dispatchers.IO) {
                db.userDao().insertUser(user)
            }


            // Збереження даних у SharedPreferences
            sharedPref.edit().apply {
                putString(EMAIL_KEY, email)
                putString(PASSWORD_KEY, password)
                putString(USERNAME_KEY, nickname)
                putString(ABOUT_KEY, about)
                putString(DOB_KEY, dob)
                apply()
            }

            Toast.makeText(requireContext(), "Реєстрація успішна!", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnBackToLogin.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etDateOfBirth.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
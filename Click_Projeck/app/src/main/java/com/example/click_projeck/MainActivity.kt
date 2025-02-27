package com.example.click_projeck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.click_projeck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ініціалізація NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Ініціалізація BottomNavigationView
        bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)

        // Отримання нікнейму з intent та передача в граф навігації
        val nickname = intent.getStringExtra("Name") ?: ""
        val bundle = Bundle().apply {
            putString("Name", nickname)
        }
        navController.setGraph(R.navigation.nav_graph, bundle)
    }
}
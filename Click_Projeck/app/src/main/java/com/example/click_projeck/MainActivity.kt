package com.example.click_projeck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.click_projeck.databinding.ActivityMainBinding
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sessionManager: SessionManager
    private lateinit var navGraph: NavGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Ініціалізація NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Отримуємо граф навігації
        val navInflater = navController.navInflater
        navGraph = navInflater.inflate(R.navigation.nav_graph)

        // Визначаємо початковий пункт призначення на основі статусу входу
        if (sessionManager.isLoggedIn()) {
            // Якщо користувач вже ввійшов у систему, переходимо на mainFragment
            navGraph.setStartDestination(R.id.mainFragment)

            // Готуємо дані користувача для mainFragment
            val userDetails = sessionManager.getUserDetails()
            val bundle = Bundle().apply {
                putString("Name", userDetails[SessionManager.KEY_USER_NAME])
            }

            // Встановлюємо граф навігації з бандлом
            navController.setGraph(navGraph, bundle)
        } else {
            // Якщо користувач не ввійшов у систему, залишаємо стандартний loginFragment
            navGraph.setStartDestination(R.id.loginFragment)
            navController.graph = navGraph
        }

        // Ініціалізація BottomNavigationView
        bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)

        // Контроль видимості нижньої навігації
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    // Приховати нижню навігацію для екранів входу та реєстрації
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    // Показати нижню навігацію для інших екранів
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }


}
package com.example.click_projeck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.click_projeck.databinding.ActivityFragmentContainerBinding

class FragmentContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentContainerBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up title changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = destination.label
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
package com.example.skillcinema

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.skillcinema.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Инициализируем Mobile Ads перед использованием
        MobileAds.initialize(this) {}

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверяем, запускался ли онбординг ранее
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("is_first_launch", true)

        // Получаем NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Загружаем навигационный граф
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        // Устанавливаем стартовый фрагмент правильно
        navGraph.setStartDestination(if (isFirstLaunch) R.id.onboardingFragment else R.id.homepageFragment)

        // Применяем граф к контроллеру
        navController.setGraph(navGraph, null)

<<<<<<< HEAD
        // Навигация без дублирования фрагментов в back stack
        binding.ivSearch.setOnClickListener {
            navigateSingleTop(R.id.searchFragment)
        }

        binding.ivHome.setOnClickListener {
            navigateSingleTop(R.id.homepageFragment)
        }

        binding.ivUser.setOnClickListener {
            navigateSingleTop(R.id.profileFragment)
=======
        binding.ivHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homepageFragment) {
                navController.navigate(R.id.homepageFragment) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        binding.ivSearch.setOnClickListener {
            if (navController.currentDestination?.id != R.id.searchFragment) {
                navController.navigate(R.id.searchFragment) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        binding.ivUser.setOnClickListener {
            if (navController.currentDestination?.id != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
>>>>>>> 4f17407e07e41c0782b70c3b7b36d9c821cc0677
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Скрывать меню на экране онбординга
            if (destination.id == R.id.onboardingFragment) {
                binding.navigationMenu.visibility = View.GONE
            } else {
                binding.navigationMenu.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateSingleTop(destinationId: Int) {
        val navOptions = navOptions {
            launchSingleTop = true
            popUpTo(destinationId) { inclusive = false }
        }
        navController.navigate(destinationId, null, navOptions)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
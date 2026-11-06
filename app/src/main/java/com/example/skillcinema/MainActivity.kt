package com.example.skillcinema

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
        setupWindowInsets()

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

        // Навигация без дублирования фрагментов в back stack
        binding.ivSearch.setOnClickListener {
            navigateSingleTop(R.id.searchFragment)
        }

        binding.ivHome.setOnClickListener {
            navigateSingleTop(R.id.homepageFragment)
        }

        binding.ivUser.setOnClickListener {
            navigateSingleTop(R.id.profileFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Скрывать меню на онбординге и экране настроек поиска
            val hideMenu = destination.id == R.id.onboardingFragment ||
                    destination.id == R.id.searchSettingsFragment
            binding.navigationMenu.visibility = if (hideMenu) View.GONE else View.VISIBLE
        }
    }

    private fun setupWindowInsets() {
        val root = binding.root
        val initialLeft = root.paddingLeft
        val initialTop = root.paddingTop
        val initialRight = root.paddingRight
        val initialBottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val safeInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout() or
                        WindowInsetsCompat.Type.ime()
            )
            // Use the original padding so repeated dispatches do not accumulate offsets.
            view.updatePadding(
                left = initialLeft + safeInsets.left,
                top = initialTop + safeInsets.top,
                right = initialRight + safeInsets.right,
                bottom = initialBottom + safeInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(root)
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

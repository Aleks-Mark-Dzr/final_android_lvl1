package com.example.skillcinema.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skillcinema.ui.onboarding.OnboardingActivityFragment

// Определяем маршруты через sealed class
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home") // Пример для будущего экрана
}

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route // Указываем стартовый экран
    ) {
        // Экран Onboarding
        composable(Screen.Onboarding.route) {
//            OnboardingActivityFragment(navController)
        }
        // Другие экраны можно добавлять здесь
        composable(Screen.Home.route) {
            // TODO: Добавьте вызов функции для экрана Home
        }
    }
}
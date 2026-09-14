package com.example.aiexpensemanager.ui.navigation

/**
 * Screen routes for bottom navigation.
 */
sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")
}

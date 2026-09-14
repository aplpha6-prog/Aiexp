package com.example.aiexpensemanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aiexpensemanager.ui.navigation.Screen
import com.example.aiexpensemanager.ui.screens.HistoryScreen
import com.example.aiexpensemanager.ui.screens.HomeScreen
import com.example.aiexpensemanager.ui.screens.SettingsScreen
import com.example.aiexpensemanager.ui.theme.AIExpenseManagerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIExpenseManagerTheme {
                MainApp(viewModel)
            }
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector
)

@Composable
fun MainApp(viewModel: ExpenseViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    val navItems = listOf(
        BottomNavItem(Screen.Home, Icons.Default.Home),
        BottomNavItem(Screen.History, Icons.Default.History),
        BottomNavItem(Screen.Settings, Icons.Default.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.screen.title) },
                        label = { Text(item.screen.title) },
                        selected = currentRoute == item.screen.route,
                        onClick = {
                            if (currentRoute != item.screen.route) {
                                navController.navigate(item.screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onInputChange = { viewModel.onInputTextChange(it) },
                    onUnderstandClick = { viewModel.parseNaturalInput() },
                    onDismissPreview = { viewModel.dismissPreview() },
                    onSaveExpense = { amount, cat, desc, type, date, origCat ->
                        viewModel.saveParsedExpense(amount, cat, desc, type, date, origCat)
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    uiState = uiState,
                    onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                    onTypeFilterChange = { viewModel.setTypeFilter(it) },
                    onDeleteExpense = { viewModel.deleteExpense(it) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    uiState = uiState,
                    onSaveBudget = { limit, enabled -> viewModel.setBudget(limit, enabled) },
                    onSaveCurrency = { viewModel.setCurrencySymbol(it) },
                    onAddLearnedRule = { kw, cat, type -> viewModel.addLearnedRule(kw, cat, type) },
                    onDeleteLearnedRule = { viewModel.deleteLearnedRule(it) }
                )
            }
        }
    }
}

package com.misaka.hoshinoschedule.ui.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.misaka.hoshinoschedule.data.settings.UserPreferences

/**
 * Voyager screens for settings navigation
 */
sealed class VoyagerSettingsScreen : Screen {
    
    data object Main : VoyagerSettingsScreen()
    
    data class Category(val category: SettingsCategory) : VoyagerSettingsScreen()
    
    data object Developer : VoyagerSettingsScreen()
    
    data object About : VoyagerSettingsScreen()
}

/**
 * The main Voyager navigator for settings
 */
@Composable
fun SettingsNavigator(
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit
) {
    Navigator(VoyagerSettingsScreen.Main) { navigator ->
        com.misaka.hoshinoschedule.ui.navigation.KiraraScreenTransition.DefaultNavigatorScreenTransition(
            navigator = navigator
        ) { screen ->
            when (screen) {
                is VoyagerSettingsScreen.Main -> SettingsMainScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    onNavigate = { navigator.push(it) },
                    onBack = onBack
                )
                
                is VoyagerSettingsScreen.Category -> SettingsCategoryScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    category = screen.category,
                    onBack = { navigator.pop() }
                )
                
                is VoyagerSettingsScreen.Developer -> SettingsDeveloperScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    onBack = { navigator.pop() }
                )
                
                is VoyagerSettingsScreen.About -> SettingsAboutScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    onBack = { navigator.pop() }
                )
            }
        }
    }
}
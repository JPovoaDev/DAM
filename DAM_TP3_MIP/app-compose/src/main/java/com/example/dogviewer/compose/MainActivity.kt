package com.example.dogviewer.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dogviewer.compose.ui.screens.DogDetailsScreen
import com.example.dogviewer.compose.ui.screens.DogGalleryScreen
import com.example.dogviewer.compose.ui.screens.FavoritesScreen
import com.example.dogviewer.compose.ui.theme.DogViewerTheme
import com.example.dogviewer.core.di.CoreInjector
import com.example.dogviewer.core.viewmodel.DogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DogApp()
                }
            }
        }
    }
}

@Composable
fun DogApp() {
    val navController = rememberNavController()

    // 1. Setup ViewModel using our DI
    // We remember the factory to avoid recreating it on recomposition
    val factory = remember { CoreInjector.provideDogViewModelFactory() }
    val dogViewModel: DogViewModel = viewModel(factory = factory)

    // 2. Setup Navigation
    NavHost(navController = navController, startDestination = "gallery") {
        
        composable("gallery") {
            DogGalleryScreen(
                viewModel = dogViewModel,
                onNavigateToDetails = { dog ->
                    navController.navigate("details/${dog.id}")
                },
                onNavigateToFavorites = {
                    navController.navigate("favorites")
                }
            )
        }

        composable(
            route = "details/{dogId}",
            arguments = listOf(navArgument("dogId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dogId = backStackEntry.arguments?.getString("dogId") ?: ""
            DogDetailsScreen(
                dogId = dogId,
                viewModel = dogViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = dogViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { dog ->
                    navController.navigate("details/${dog.id}")
                }
            )
        }
    }
}

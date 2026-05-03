package com.example.dogviewer.compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dogviewer.core.model.ImageItem
import com.example.dogviewer.core.viewmodel.DogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: DogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (ImageItem) -> Unit
) {
    // In a real app with Compose, the repository should return a Flow<List<ImageItem>>.
    // For this port, we will simulate observation by remembering the favorites and re-fetching on composition.
    var favorites by remember { mutableStateOf(viewModel.getFavorites()) }

    // Re-fetch when we return to this screen to reflect changes made in Details screen
    LaunchedEffect(Unit) {
        favorites = viewModel.getFavorites()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Favorites") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (favorites.isEmpty()) {
                Text(
                    text = "No favorites yet.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favorites, key = { it.id }) { dog ->
                        DogGridItem(
                            dog = dog,
                            initialIsFavorite = true, // By definition, it's a favorite here
                            onToggleFavorite = { isFavorite ->
                                if (!isFavorite) {
                                    viewModel.removeFavorite(dog)
                                    // Trigger recomposition by re-fetching
                                    favorites = viewModel.getFavorites()
                                }
                            },
                            onNavigateToDetails = { onNavigateToDetails(dog) }
                        )
                    }
                }
            }
        }
    }
}

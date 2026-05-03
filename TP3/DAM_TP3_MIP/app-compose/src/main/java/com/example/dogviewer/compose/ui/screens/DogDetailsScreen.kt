package com.example.dogviewer.compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.dogviewer.core.model.ImageItem
import com.example.dogviewer.core.viewmodel.DogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogDetailsScreen(
    dogId: String,
    viewModel: DogViewModel,
    onNavigateBack: () -> Unit
) {
    val dog = viewModel.getDogById(dogId)
    
    // We keep a local state that initializes from the viewModel, but reacts to changes.
    // However, since viewModel.isFavorite is a regular function reading from repository,
    // in Compose we'd ideally observe a Flow. For this simple port, we can just use a local state
    // initialized from the viewModel and updated when the button is clicked.
    var isFavorite by remember(dogId) { 
        mutableStateOf(dog?.let { viewModel.isFavorite(it) } ?: false) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dog Details") },
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
        if (dog == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Dog not found.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SubcomposeAsyncImage(
                model = dog.url,
                contentDescription = dog.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
                loading = {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = dog.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(text = "Breed: ${dog.breed}", style = MaterialTheme.typography.titleMedium)
                if (dog.subBreed != null) {
                    Text(text = "Sub-breed: ${dog.subBreed}", style = MaterialTheme.typography.titleMedium)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ID: ${dog.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "URL: ${dog.url}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (isFavorite) {
                            viewModel.removeFavorite(dog)
                            isFavorite = false
                        } else {
                            viewModel.addFavorite(dog)
                            isFavorite = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isFavorite) "Remove Favorite" else "Add to Favorites")
                }
            }
        }
    }
}

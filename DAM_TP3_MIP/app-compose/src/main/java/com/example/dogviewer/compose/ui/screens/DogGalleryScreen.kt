package com.example.dogviewer.compose.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.dogviewer.core.model.ImageItem
import com.example.dogviewer.core.viewmodel.DogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogGalleryScreen(
    viewModel: DogViewModel,
    onNavigateToDetails: (ImageItem) -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val images by viewModel.images.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)
    
    val favoritesCount = viewModel.getFavoritesCount()

    // Fetch initial image if list is empty
    LaunchedEffect(Unit) {
        if (images.isEmpty()) {
            viewModel.fetchNewDogImage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dog Viewer Compose") },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.fetchNewDogImage() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Fetch New Dog")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                // Favorites Hub Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onNavigateToFavorites() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = if (favoritesCount > 0) "You have $favoritesCount dogs in your collection" else "View your favorited dogs",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Exclusive Compose Feature: Adaptive Grid Layout + AnimatedVisibility
                AnimatedVisibility(
                    visible = images.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(images, key = { it.id }) { dog ->
                            DogGridItem(
                                dog = dog,
                                initialIsFavorite = viewModel.isFavorite(dog),
                                onToggleFavorite = { newState ->
                                    if (newState) viewModel.addFavorite(dog) else viewModel.removeFavorite(dog)
                                },
                                onNavigateToDetails = { onNavigateToDetails(dog) }
                            )
                        }
                    }
                }
            }

            // Exclusive Compose Feature: Animated Loading Overlay
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Error Message Snackbar could be implemented here as well
            errorMessage?.let { msg ->
                // Showing a simple text box for the error for simplicity
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
fun DogGridItem(
    dog: ImageItem, 
    initialIsFavorite: Boolean,
    onToggleFavorite: (Boolean) -> Unit,
    onNavigateToDetails: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isFavorited by remember(initialIsFavorite) { mutableStateOf(initialIsFavorite) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) // Feature Exclusiva: Expansão Suave do Card
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                SubcomposeAsyncImage(
                    model = dog.url,
                    contentDescription = dog.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                )
                
                // Title overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = dog.title,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
            
            // Feature Exclusiva: Conteúdo que aparece quando expandido
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        isFavorited = !isFavorited
                        onToggleFavorite(isFavorited)
                    }) {
                        Icon(
                            imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Toggle",
                            tint = if (isFavorited) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(onClick = onNavigateToDetails) {
                        Text("Ver Detalhes")
                    }
                }
            }
        }
    }
}

package com.example.dogviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dogviewer.databinding.ActivityImageDetailsBinding
import com.example.dogviewer.model.ImageItem
import com.example.dogviewer.network.ApiClient

/**
 * Secondary Activity responsible for rendering a zoomed-in perspective 
 * and allowing the user to favorite/un-favorite the dog.
 */
class ImageDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Enable Native Back Navigation
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Dog Details")

        // 2. Receive data
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE_URL") ?: ""
        val imageTitle = intent.getStringExtra("EXTRA_IMAGE_TITLE") ?: "Unknown Breed"
        val breed = intent.getStringExtra("EXTRA_IMAGE_BREED") ?: "Unknown"
        val subBreed = intent.getStringExtra("EXTRA_IMAGE_SUBBREED")
        val imageId = intent.getStringExtra("EXTRA_IMAGE_ID") ?: java.util.UUID.randomUUID().toString()
        
        // Correct instantiation with full data set including ID
        val currentItem = ImageItem(
            url = imageUrl, 
            title = imageTitle,
            breed = breed,
            subBreed = subBreed,
            id = imageId
        )

        binding.breedTitleText.text = imageTitle
        binding.idTextView.text = imageId
        binding.urlTextView.text = imageUrl
        
        binding.breedTextView.text = breed
        if (subBreed != null) {
            binding.subBreedSection.visibility = android.view.View.VISIBLE
            binding.subBreedTextView.text = subBreed
        } else {
            binding.subBreedSection.visibility = android.view.View.GONE
        }

        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                .placeholder(R.color.surfaceVariant)
                .into(binding.fullImageView)
        }
        
        // 3. Setup Favorite Toggle Logic
        updateFavoriteButtonState(currentItem)

        binding.favoriteButton.setOnClickListener {
            val repository = ApiClient.dogRepository
            val favorites = repository.getFavorites()
            val isFavorited = favorites.any { it.url == currentItem.url }

            if (isFavorited) {
                repository.removeFavorite(currentItem)
            } else {
                repository.addFavorite(currentItem)
            }
            updateFavoriteButtonState(currentItem)
        }
    }

    private fun updateFavoriteButtonState(item: ImageItem) {
        val isFavorited = ApiClient.dogRepository.getFavorites().any { it.url == item.url }
        if (isFavorited) {
            binding.favoriteButton.text = "Remove Favorite"
            binding.favoriteButton.setIconResource(R.drawable.ic_favorite)
        } else {
            binding.favoriteButton.text = "Add to Favorites"
            binding.favoriteButton.setIconResource(R.drawable.ic_favorite) // In a real app, maybe a heart-border icon
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

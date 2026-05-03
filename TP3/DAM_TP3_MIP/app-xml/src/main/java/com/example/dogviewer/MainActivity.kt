package com.example.dogviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dogviewer.databinding.ActivityMainBinding
import com.example.dogviewer.core.di.CoreInjector
import com.example.dogviewer.ui.adapter.DogAdapter
import com.example.dogviewer.core.viewmodel.DogViewModel
import com.example.dogviewer.core.viewmodel.DogViewModelFactory

import android.view.Menu
import android.view.MenuItem
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DogViewModel
    private lateinit var adapter: DogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the toolbar as the ActionBar to support menus
        setSupportActionBar(binding.toolbar)

        adapter = DogAdapter()
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        // 2. Setup ViewModel using the CoreInjector
        val factory = CoreInjector.provideDogViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[DogViewModel::class.java]

        // 3. Observe LiveData Streams
        viewModel.images.observe(this) { imagesList ->
            adapter.updateData(imagesList)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading && !binding.swipeRefresh.isRefreshing) View.VISIBLE else View.GONE
            binding.swipeRefresh.isRefreshing = isLoading
        }
        
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_LONG).show()
                binding.swipeRefresh.isRefreshing = false
            }
        }
        
        // 4. Initial load so the user sees an image immediately
        if (viewModel.images.value.isNullOrEmpty()) {
            viewModel.fetchNewDogImage()
        }

        // 5. Wire the UI Interactions
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchNewDogImage()
        }

        binding.refreshFab.setOnClickListener {
            viewModel.fetchNewDogImage()
            // Optional: Smooth scroll to bottom to see the new item
            binding.recyclerView.smoothScrollToPosition(adapter.itemCount)
        }

        binding.favoritesHubCard.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        refreshFavoritesCount()
    }

    override fun onResume() {
        super.onResume()
        refreshFavoritesCount()
    }

    private fun refreshFavoritesCount() {
        val count = viewModel.getFavoritesCount()
        binding.favoritesCountText.text = if (count > 0) {
            "You have $count dogs in your collection"
        } else {
            "View your favorited dogs"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

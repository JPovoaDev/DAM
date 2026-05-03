package com.example.dogviewer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dogviewer.databinding.ActivityFavoritesBinding
import androidx.lifecycle.ViewModelProvider
import com.example.dogviewer.core.di.CoreInjector
import com.example.dogviewer.core.viewmodel.DogViewModel
import com.example.dogviewer.ui.adapter.DogAdapter

/**
 * Window responsible for displaying the current list of FIFO favorites.
 */
class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: DogAdapter
    private lateinit var viewModel: DogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup Toolbar with Back Navigation
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 2. Setup RecyclerView
        adapter = DogAdapter()
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        // 3. Setup ViewModel
        val factory = CoreInjector.provideDogViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[DogViewModel::class.java]

        // 4. Load Data
        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        // Refresh every time we return to this screen in case a favorite was removed in Details
        loadFavorites()
    }

    private fun loadFavorites() {
        val favorites = viewModel.getFavorites()
        if (favorites.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.favoritesBanner.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.favoritesBanner.visibility = View.VISIBLE
            adapter.updateData(favorites)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

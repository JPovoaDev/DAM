package com.example.footballscout.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.footballscout.FootballScoutApp
import com.example.footballscout.databinding.FragmentFavoriteListBinding
import com.example.footballscout.ui.adapters.PlayerSearchAdapter
import com.example.footballscout.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch

class FavoritePlayersFragment : Fragment() {

    private var _binding: FragmentFavoriteListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels({ requireParentFragment() }) {
        val app = requireActivity().application as FootballScoutApp
        FavoritesViewModel.Factory(app.container.playerRepository, app.container.teamRepository)
    }

    private lateinit var adapter: PlayerSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = PlayerSearchAdapter { player ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToPlayerDetailFragment(player.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritePlayers.collect { players ->
                    adapter.submitList(players)
                    binding.emptyText.isVisible = players.isEmpty()
                    binding.recyclerView.isVisible = players.isNotEmpty()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

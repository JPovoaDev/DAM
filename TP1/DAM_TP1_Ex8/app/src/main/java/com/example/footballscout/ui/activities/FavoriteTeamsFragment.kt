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
import com.example.footballscout.ui.adapters.TeamSearchAdapter
import com.example.footballscout.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch

class FavoriteTeamsFragment : Fragment() {

    private var _binding: FragmentFavoriteListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels({ requireParentFragment() }) {
        val app = requireActivity().application as FootballScoutApp
        FavoritesViewModel.Factory(app.container.playerRepository, app.container.teamRepository)
    }

    private lateinit var adapter: TeamSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = TeamSearchAdapter { team ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToTeamDetailFragment(team.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteTeams.collect { teams ->
                    adapter.submitList(teams)
                    binding.emptyText.isVisible = teams.isEmpty()
                    binding.recyclerView.isVisible = teams.isNotEmpty()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

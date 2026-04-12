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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.footballscout.FootballScoutApp
import com.example.footballscout.R
import com.example.footballscout.databinding.FragmentTeamDetailBinding
import com.example.footballscout.ui.adapters.MatchAdapter
import com.example.footballscout.ui.adapters.StandingAdapter
import com.example.footballscout.viewmodel.TeamDetailViewModel
import com.example.footballscout.data.model.Team
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.launch

class TeamDetailFragment : Fragment() {

    private var _binding: FragmentTeamDetailBinding? = null
    private val binding get() = _binding!!

    private val args: TeamDetailFragmentArgs by navArgs()

    private val viewModel: TeamDetailViewModel by viewModels {
        val app = requireActivity().application as FootballScoutApp
        TeamDetailViewModel.Factory(app.container.teamRepository)
    }

    private lateinit var nextMatchesAdapter: MatchAdapter
    private lateinit var prevMatchesAdapter: MatchAdapter
    private lateinit var standingAdapter: StandingAdapter

    private var currentTeam: Team? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupAdapters()
        
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadTeamData(args.teamId)
        }
        
        viewModel.loadTeamData(args.teamId)
        
        observeViewModel()

        binding.fabFavorite.setOnClickListener {
            currentTeam?.let { team ->
                viewModel.toggleFavorite(team)
            }
        }
    }

    private fun setupAdapters() {
        nextMatchesAdapter = MatchAdapter()
        binding.recyclerNextMatches.adapter = nextMatchesAdapter

        prevMatchesAdapter = MatchAdapter()
        binding.recyclerPrevMatches.adapter = prevMatchesAdapter

        standingAdapter = StandingAdapter()
        binding.recyclerStandings.adapter = standingAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Team Details
                launch {
                    viewModel.teamState.collect { resource ->
                        when (resource) {
                            is Resource.Loading<*> -> {
                                if (!binding.swipeRefresh.isRefreshing) {
                                    binding.progressBar.isVisible = true
                                    binding.swipeRefresh.isVisible = false
                                }
                                binding.errorText.isVisible = false
                            }
                            is Resource.Success<*> -> {
                                binding.swipeRefresh.isRefreshing = false
                                binding.progressBar.isVisible = false
                                binding.swipeRefresh.isVisible = true
                                resource.data?.let { team ->
                                    currentTeam = team
                                    populateUI(team)
                                }
                            }
                            is Resource.Error<*> -> {
                                binding.swipeRefresh.isRefreshing = false
                                binding.progressBar.isVisible = false
                                binding.swipeRefresh.isVisible = false
                                binding.errorText.isVisible = true
                                binding.errorText.text = resource.message
                            }
                        }
                    }
                }
                
                // Next Matches
                launch {
                    viewModel.nextMatches.collect { resource ->
                        if (resource is Resource.Success) {
                            nextMatchesAdapter.submitList(resource.data)
                        }
                    }
                }

                // Previous Matches
                launch {
                    viewModel.previousMatches.collect { resource ->
                        if (resource is Resource.Success) {
                            prevMatchesAdapter.submitList(resource.data)
                        }
                    }
                }

                // Standings
                launch {
                    viewModel.standings.collect { resource ->
                        if (resource is Resource.Success) {
                            standingAdapter.submitList(resource.data)
                        }
                    }
                }

                // Favorite
                launch {
                    viewModel.isFavorite.collect { isFav ->
                        updateFavoriteIcon(isFav)
                    }
                }
            }
        }
    }

    private fun populateUI(team: Team) {
        binding.collapsingToolbar.title = team.name
        binding.teamLeague.text = team.league.ifEmpty { "N/A" }
        binding.teamStadium.text = team.stadium.ifEmpty { "N/A" }
        binding.teamDescription.text = team.description.ifEmpty { "No description available." }

        Glide.with(this)
            .load(team.logoUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.teamCoverImage)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        binding.fabFavorite.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

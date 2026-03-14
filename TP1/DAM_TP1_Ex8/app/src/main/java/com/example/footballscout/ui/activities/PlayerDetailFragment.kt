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
import com.example.footballscout.databinding.FragmentPlayerDetailBinding
import com.example.footballscout.data.model.Player
import com.example.footballscout.utils.Resource
import com.example.footballscout.viewmodel.PlayerDetailViewModel
import kotlinx.coroutines.launch

class PlayerDetailFragment : Fragment() {

    private var _binding: FragmentPlayerDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PlayerDetailFragmentArgs by navArgs()

    private val viewModel: PlayerDetailViewModel by viewModels {
        val app = requireActivity().application as FootballScoutApp
        PlayerDetailViewModel.Factory(app.container.playerRepository)
    }

    private var currentPlayer: Player? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadPlayer(args.playerId)
        }

        viewModel.loadPlayer(args.playerId)

        observeViewModel()
        
        binding.fabFavorite.setOnClickListener {
            currentPlayer?.let { player ->
                viewModel.toggleFavorite(player)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.playerState.collect { resource ->
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
                                resource.data?.let { player ->
                                    currentPlayer = player
                                    populateUI(player)
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
                launch {
                    viewModel.isFavorite.collect { isFav ->
                        updateFavoriteIcon(isFav)
                    }
                }
            }
        }
    }

    private fun populateUI(player: Player) {
        binding.collapsingToolbar.title = player.name
        binding.playerNationality.text = player.nationality.ifEmpty { "N/A" }
        binding.playerDob.text = player.dateOfBirth.ifEmpty { "N/A" }
        binding.playerHeight.text = player.height.ifEmpty { "N/A" }
        binding.playerWeight.text = player.weight.ifEmpty { "N/A" }
        binding.playerTeam.text = player.team.ifEmpty { "N/A" }


        // Setup the timeline Recycler View
        val formerTeamAdapter = com.example.footballscout.ui.adapters.FormerTeamAdapter()
        binding.recyclerFormerTeams.adapter = formerTeamAdapter
        formerTeamAdapter.submitList(player.formerTeams)

        Glide.with(this)
            .load(player.photoUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.playerCoverImage)
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

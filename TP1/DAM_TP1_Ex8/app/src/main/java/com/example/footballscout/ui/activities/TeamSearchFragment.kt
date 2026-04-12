package com.example.footballscout.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.footballscout.databinding.FragmentTeamSearchBinding
import com.example.footballscout.ui.adapters.TeamSearchAdapter
import com.example.footballscout.viewmodel.TeamSearchViewModel
import com.example.footballscout.utils.Resource
import kotlinx.coroutines.launch

class TeamSearchFragment : Fragment() {

    private var _binding: FragmentTeamSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TeamSearchViewModel by viewModels {
        val app = requireActivity().application as FootballScoutApp
        TeamSearchViewModel.Factory(app.container.teamRepository)
    }
    
    private lateinit var adapter: TeamSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchInput()
        setupSwipeRefresh()
        observeViewModel()
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            val query = binding.searchEditText.text.toString()
            viewModel.searchTeams(query)
        }
    }
    
    private fun setupRecyclerView() {
        adapter = TeamSearchAdapter { team ->
            val action = TeamSearchFragmentDirections
                .actionTeamSearchFragmentToTeamDetailFragment(team.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchTeams(s.toString())
            }
        })
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect { resource ->
                    when (resource) {
                        is Resource.Loading<*> -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.progressBar.isVisible = true
                                binding.recyclerView.isVisible = false
                            }
                            binding.errorText.isVisible = false
                        }
                        is Resource.Success<*> -> {
                            android.util.Log.d("TeamSearchFragment", "Success! Items returned: ${resource.data?.size}")
                            binding.swipeRefresh.isRefreshing = false
                            binding.progressBar.isVisible = false
                            binding.recyclerView.isVisible = true
                            binding.errorText.isVisible = resource.data?.isEmpty() == true && binding.searchEditText.text?.isNotEmpty() == true
                            if (binding.errorText.isVisible) {
                                binding.errorText.text = "No results found."
                            }
                            adapter.submitList(resource.data)
                        }
                        is Resource.Error<*> -> {
                            binding.swipeRefresh.isRefreshing = false
                            binding.progressBar.isVisible = false
                            binding.recyclerView.isVisible = false
                            binding.errorText.isVisible = true
                            binding.errorText.text = resource.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

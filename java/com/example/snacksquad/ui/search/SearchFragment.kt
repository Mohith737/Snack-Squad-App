package com.example.snacksquad.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snacksquad.adapter.PopularAdapter
import com.example.snacksquad.R
import com.example.snacksquad.data.repository.RepositoryProvider
import com.example.snacksquad.databinding.FragmentSearchBinding
import com.example.snacksquad.ui.cart.CartViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = checkNotNull(_binding)

    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModel.provideFactory(RepositoryProvider.foodRepository)
    }
    private val cartViewModel: CartViewModel by viewModels {
        CartViewModel.provideFactory(
            RepositoryProvider.cartRepository,
            RepositoryProvider.getOrderHistoryRepository(requireContext())
        )
    }
    private val searchResultsAdapter = PopularAdapter { item ->
        cartViewModel.addToCart(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchUi()
        observeSearchResults()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSearchUi() {
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResultsRecyclerView.adapter = searchResultsAdapter

        binding.searchView.editText.doAfterTextChanged { editable ->
            searchViewModel.updateQuery(editable?.toString().orEmpty())
        }
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    searchViewModel.query.collect { query ->
                        if (binding.searchView.editText.text?.toString() != query) {
                            binding.searchView.editText.setText(query)
                            binding.searchView.editText.setSelection(query.length)
                        }
                        binding.searchBar.setText(query)
                    }
                }
                launch {
                    searchViewModel.filteredResults.collect { results ->
                        searchResultsAdapter.submitList(results)
                        binding.searchEmptyStateTextView.isVisible = results.isEmpty()
                        binding.searchResultsRecyclerView.isVisible = results.isNotEmpty()
                    }
                }
            }
        }
    }
}



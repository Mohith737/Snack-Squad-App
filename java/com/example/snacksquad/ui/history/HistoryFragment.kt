package com.example.snacksquad.ui.history

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snacksquad.adapter.HistoryAdapter
import com.example.snacksquad.data.repository.RepositoryProvider
import com.example.snacksquad.databinding.FragmentHistoryBinding
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding
        get() = checkNotNull(_binding)

    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModel.provideFactory(
            RepositoryProvider.getOrderHistoryRepository(requireContext())
        )
    }
    private val historyAdapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter
        observeOrderHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeOrderHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.orderHistories.collect { orderHistories ->
                    historyAdapter.submitList(orderHistories)
                    binding.emptyHistoryTextView.isVisible = orderHistories.isEmpty()
                    binding.historyRecyclerView.isVisible = orderHistories.isNotEmpty()
                }
            }
        }
    }
}

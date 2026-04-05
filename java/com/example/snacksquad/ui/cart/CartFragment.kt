package com.example.snacksquad.ui.cart

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
import com.example.snacksquad.adapter.CartAdapter
import com.example.snacksquad.data.repository.RepositoryProvider
import com.example.snacksquad.databinding.FragmentCartBinding
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding
        get() = checkNotNull(_binding)

    private val cartViewModel: CartViewModel by viewModels {
        CartViewModel.provideFactory(
            RepositoryProvider.cartRepository,
            RepositoryProvider.getOrderHistoryRepository(requireContext())
        )
    }
    private val cartAdapter = CartAdapter(
        onRemoveItem = { position -> cartViewModel.removeFromCart(position) },
        onIncreaseQuantity = { position -> cartViewModel.increaseQuantity(position) },
        onDecreaseQuantity = { position -> cartViewModel.decreaseQuantity(position) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartList()
        observeCart()
        binding.placeOrderButton.setOnClickListener {
            cartViewModel.placeOrder(cartViewModel.cartItems.value)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCartList() {
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.setItemViewCacheSize(20)
        binding.cartRecyclerView.isDrawingCacheEnabled = true
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartItems.collect { items ->
                    cartAdapter.submitList(items)
                    binding.placeOrderButton.isEnabled = items.isNotEmpty()
                    binding.cartEmptyStateTextView.isVisible = items.isEmpty()
                    binding.cartRecyclerView.isVisible = items.isNotEmpty()
                }
            }
        }
    }
}

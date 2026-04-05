package com.example.snacksquad.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.snacksquad.R
import com.example.snacksquad.adapter.PopularAdapter
import com.example.snacksquad.data.repository.RepositoryProvider
import com.example.snacksquad.databinding.FragmentHomeBinding
import com.example.snacksquad.ui.cart.CartViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = checkNotNull(_binding)

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.provideFactory(RepositoryProvider.foodRepository)
    }
    private val cartViewModel: CartViewModel by viewModels {
        CartViewModel.provideFactory(
            RepositoryProvider.cartRepository,
            RepositoryProvider.getOrderHistoryRepository(requireContext())
        )
    }
    private val popularAdapter = PopularAdapter { item ->
        cartViewModel.addToCart(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupImageSlider()
        setupPopularItems()
        observePopularItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupImageSlider() {
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }

    private fun setupPopularItems() {
        binding.PopulerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopulerRecyclerView.setItemViewCacheSize(20)
        binding.PopulerRecyclerView.isDrawingCacheEnabled = true
        binding.PopulerRecyclerView.adapter = popularAdapter
    }

    private fun observePopularItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.popularItems.collect { items ->
                    popularAdapter.submitList(items)
                }
            }
        }
    }
}

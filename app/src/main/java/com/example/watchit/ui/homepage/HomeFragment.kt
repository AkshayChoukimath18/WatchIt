package com.example.watchit.ui.homepage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.watchit.R
import com.example.watchit.ui.addMovie.AddMovieFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var viewPagerCarousel: ViewPager2
    private lateinit var tabDots: TabLayout

    private lateinit var recyclerSelections: RecyclerView

    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var fabAddLibrary: FloatingActionButton

    private val selectionAdapter by lazy {
        HomeSelectionAdapter(onMovieClick = { movie ->
            Toast.makeText(requireContext(), movie.title, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerCarousel = view.findViewById(R.id.viewPagerCarousel)
        tabDots = view.findViewById(R.id.tabDots)
        recyclerSelections = view.findViewById(R.id.recyclerSelections)
        recyclerSelections.adapter = selectionAdapter
        fabAddLibrary = view.findViewById(R.id.fabAddLibrary)
        fabAddLibrary.setOnClickListener {
            val action = AddMovieFragmentDirections.actionGlobalAddMovieFragment(libraryId = -1L, sharedUrl = "")
            findNavController().navigate(action)
        }


        setupCarousel()
        loadDummyData()
    }

    private fun loadDummyData() {
        viewModel.getMoviesWithLibrary()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect { state ->
                    selectionAdapter.submitList(state.sections)
                }
            }
        }





    }

    fun setupCarousel(){

        carouselAdapter = CarouselAdapter{ movie ->
            Toast.makeText(requireContext(), movie.title.toString(), Toast.LENGTH_SHORT).show()
        }
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                val carouselMovie = state.sections
                    .flatMap { it.movies }
                    .distinctBy { it.id }
                    .take(10)
                carouselAdapter.submitList(carouselMovie)
            }
        }
        viewPagerCarousel.adapter = carouselAdapter
        viewPagerCarousel.offscreenPageLimit = 3

        val pageMarginPx = resources.getDimensionPixelSize(R.dimen.carousel_page_margin)
        val pageOffsetPx = resources.getDimensionPixelSize(R.dimen.carousel_page_offset)
        viewPagerCarousel.setPageTransformer { page, position ->
            val offset = position * -(2 * pageOffsetPx + pageMarginPx)
            page.scaleY = 0.9f + (1 - kotlin.math.abs(position)) * 0.1f
        }

        TabLayoutMediator(tabDots, viewPagerCarousel) {tabs: TabLayout.Tab, _: Int  ->
            tabs.setCustomView(R.layout.item_dot)
        }.attach()

        tabDots.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.setBackgroundResource(R.drawable.dot_shape_selected)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.setBackgroundResource(R.drawable.dot_shape)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}